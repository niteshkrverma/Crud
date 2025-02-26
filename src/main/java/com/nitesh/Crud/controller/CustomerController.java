package com.nitesh.Crud.controller;

import com.nitesh.Crud.models.Customers;
import com.nitesh.Crud.services.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("*") // Allow frontend access
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String UPLOAD_DIR = "public/uploads/";;

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
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File uploadFile = new File(UPLOAD_DIR + fileName);
            file.transferTo(uploadFile);

            Customers customer = new Customers(name, address, email, pwd, number, fileName);
            Customers savedCustomer = customerRepository.save(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update Customer
    @PutMapping("/{id}")
    public ResponseEntity<Customers> updateCustomer(@PathVariable int id, @RequestBody Customers updatedCustomer) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(updatedCustomer.getName());
                    existingCustomer.setAddress(updatedCustomer.getAddress());
                    existingCustomer.setEmail(updatedCustomer.getEmail());
                    existingCustomer.setPwd(updatedCustomer.getPwd());
                    existingCustomer.setNumber(updatedCustomer.getNumber());
                    customerRepository.save(existingCustomer);
                    return ResponseEntity.ok(existingCustomer);
                }).orElseGet(() -> ResponseEntity.notFound().build());
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

    // Serve Image File
    @GetMapping("/image/{filename}")
    public ResponseEntity<String> getImage(@PathVariable String filename) {
        return ResponseEntity.ok("/uploads/" + filename);
    }
}
