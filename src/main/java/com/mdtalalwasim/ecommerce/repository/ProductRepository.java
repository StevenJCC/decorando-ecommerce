package com.mdtalalwasim.ecommerce.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mdtalalwasim.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrue();

    List<Product> findByProductCategory(String category);
}
