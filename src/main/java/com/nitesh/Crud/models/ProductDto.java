package com.nitesh.Crud.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProductDto {
    @NotEmpty(message = "The name is required")
    private String name;

    @NotEmpty(message = "The brand is required")
    private String brand;
    @NotEmpty(message = "The category is required")
    private String category;
    @NotEmpty(message = "The description is required")
    private String description;

    @Min(0)
    private double price;
    private MultipartFile imageFile;

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public @Min(0) double getPrice() {
        return price;
    }

    public void setPrice(@Min(0) double price) {
        this.price = price;
    }

    public @NotEmpty(message = "The description is required") String getDescription() {
        return description;
    }

    public void setDescription(@NotEmpty(message = "The description is required") String description) {
        this.description = description;
    }

    public @NotEmpty(message = "The category is required") String getCategory() {
        return category;
    }

    public void setCategory(@NotEmpty(message = "The category is required") String category) {
        this.category = category;
    }

    public @NotEmpty(message = "The brand is required") String getBrand() {
        return brand;
    }

    public void setBrand(@NotEmpty(message = "The brand is required") String brand) {
        this.brand = brand;
    }

    public @NotEmpty(message = "The name is required") String getName() {
        return name;
    }

    public void setName(@NotEmpty(message = "The name is required") String name) {
        this.name = name;
    }
}
