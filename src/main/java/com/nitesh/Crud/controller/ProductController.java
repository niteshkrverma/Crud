package com.nitesh.Crud.controller;

import com.nitesh.Crud.models.ProductDto;
import com.nitesh.Crud.models.Products;
import com.nitesh.Crud.services.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Products> product = repo.findAll();
        model.addAttribute("product", product);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/createProducts";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        // Check if image file is empty and add error message
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required."));
        }

        // If there are any validation errors, return the same page with error messages
        if (result.hasErrors()) {
            return "products/createProducts";
        }

        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String strongFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            // Set up the upload directory
            String uploadDir = "public/img";
            Path uploadPath = Paths.get(uploadDir);

            // Create directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Use InputStream to copy the file
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(strongFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Products product = new Products();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatatedAt(createdAt);
        product.setImageFileName(strongFileName);
        repo.save(product);

        // Redirect to product list page after successful creation
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(Model model, @PathVariable int id){
        try {
            // Fetch the product to edit by id
            Optional<Products> productOpt = repo.findById(id);
            if (productOpt.isEmpty()) {
                return "redirect:/products"; // Redirect if the product is not found
            }

            Products product = productOpt.get();
            model.addAttribute("product", product);

            // Populate ProductDto with existing product data
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
//            productDto.setImageFile(product.getImageFile()); // If you want to display image info

            model.addAttribute("productDto", productDto);
        }
        catch (Exception ex){
            System.out.println("Exception : " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct"; // Return to the EditProduct page
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        // Check if there are validation errors
        if (result.hasErrors()) {
            return "products/editProduct"; // Return to the edit page if there are errors
        }

        Optional<Products> productOpt = repo.findById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/products"; // Redirect if the product is not found
        }

        Products product = productOpt.get();

        // Handle the image upload only if a new file is selected
        if (!productDto.getImageFile().isEmpty()) {
            MultipartFile image = productDto.getImageFile();
            String strongFileName = new Date().getTime() + "_" + image.getOriginalFilename();
            try {
                String uploadDir = "public/img";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(strongFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                // Set the new image filename in the product
                product.setImageFileName(strongFileName);
            } catch (Exception ex) {
                System.out.println("Error uploading image: " + ex.getMessage());
            }
        }

        // Update the rest of the product fields
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        repo.save(product); // Save the updated product

        return "redirect:/products"; // Redirect to the products list
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        try {
            // Find the product by ID
            Products product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

            // Delete the product
            repo.delete(product);

            // Optionally, delete the image file if you want to remove it from the server
            String imageFilePath = "public/img/" + product.getImageFileName();
            Files.deleteIfExists(Paths.get(imageFilePath));

        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }

        // After deletion, redirect to the product list
        return "redirect:/products";
    }
}
