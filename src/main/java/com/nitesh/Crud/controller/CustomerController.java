package com.nitesh.Crud.controller;

import com.nitesh.Crud.models.Customers;
import com.nitesh.Crud.services.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("*") // Allow frontend access
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String UPLOAD_DIR = "public/uploads/"; // Directory to store uploaded files

    // Get All Customers
    @GetMapping
    public List<Customers> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get Customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customers> getCustomerById(@PathVariable int id) {
        Optional<Customers> customer = customerRepository.findById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add Customer with Image Upload
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Customers> addCustomer(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("email") String email,
            @RequestParam("pwd") String pwd,
            @RequestParam("number") long number,
            @RequestParam("image") MultipartFile file) {

        try {
            // Validate image file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // Set up the upload directory
            String uploadDir = UPLOAD_DIR;
            Path uploadPath = Paths.get(uploadDir);

            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique file name to avoid conflicts
            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Save the file to the server
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Create a new customer object
            Customers customer = new Customers(name, address, email, pwd, number, uniqueFileName);

            // Save the customer to the repository
            Customers savedCustomer = customerRepository.save(customer);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update Customer
    @PutMapping("/{id}")
    public ResponseEntity<Customers> updateCustomer(
            @PathVariable int id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "pwd", required = false) String pwd,
            @RequestParam(value = "number", required = false) Long number,
            @RequestParam(value = "image", required = false) MultipartFile file) {

        Optional<Customers> existingCustomerOptional = customerRepository.findById(id);

        if (!existingCustomerOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Customers existingCustomer = existingCustomerOptional.get();

        // Update fields if new values are provided
        if (name != null) existingCustomer.setName(name);
        if (address != null) existingCustomer.setAddress(address);
        if (email != null) existingCustomer.setEmail(email);
        if (pwd != null) existingCustomer.setPwd(pwd);  // Update password
        if (number != null) existingCustomer.setNumber(number);

        // Handle image upload if a new image is provided
        if (file != null && !file.isEmpty()) {
            // Delete the old image if exists (optional step if you want to remove the old file)
            String oldImageFileName = existingCustomer.getImage();
            if (oldImageFileName != null) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR, oldImageFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Save the new image
            try {
                String uploadDir = UPLOAD_DIR;
                Path uploadPath = Paths.get(uploadDir);

                // Create the directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate a unique file name to avoid conflicts
                String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(uniqueFileName);

                // Save the file to the server
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Set the new image file name in the customer object
                existingCustomer.setImage(uniqueFileName);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        // Save the updated customer to the repository
        customerRepository.save(existingCustomer);

        return ResponseEntity.ok(existingCustomer);
    }

    // Delete Customer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Serve Image File (To display customer image)
    @GetMapping("/image/{filename}")
    public ResponseEntity<String> getImage(@PathVariable String filename) {
        // Ensure the file is served from the static path "/uploads/"
        return ResponseEntity.ok("/uploads/" + filename);
    }
}
