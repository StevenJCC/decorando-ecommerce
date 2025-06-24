package com.mdtalalwasim.ecommerce.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "category") // Cambiado para coincidir con el nombre de la tabla en SQL
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "category_name") // Aseguramos que coincida con el nombre en SQL
    private String categoryName;
    
    @Column(name = "category_image") // Aseguramos que coincida con el nombre en SQL
    private String categoryImage;
    
    @Column(name = "is_active") // Aseguramos que coincida con el nombre en SQL
    private Boolean isActive;
    
    @Column(name = "created_at") // Aseguramos que coincida con el nombre en SQL
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at") // Aseguramos que coincida con el nombre en SQL
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
    
    // Constructores
    public Category() {
    }
    
    public Category(Long id, String categoryName, String categoryImage, Boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt, List<Product> products) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.products = products;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public String getCategoryImage() {
        return categoryImage;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}