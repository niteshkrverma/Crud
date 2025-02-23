package com.nitesh.Crud.services;

import com.nitesh.Crud.models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Products,Integer> {

}
