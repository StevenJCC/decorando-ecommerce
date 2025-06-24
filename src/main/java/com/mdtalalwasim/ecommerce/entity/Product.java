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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "product") // Cambiado para coincidir con el nombre de la tabla en SQL
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_title", length = 500) // Aseguramos que coincida con el SQL
    private String productTitle;
    
    @Column(name = "product_description", length = 5000) // Aseguramos que coincida con el SQL
    private String productDescription;
    
    @Column(name = "product_price") // Aseguramos que coincida con el SQL
    private double productPrice;
    
    @Column(name = "product_stock") // Aseguramos que coincida con el SQL
    private int productStock;
    
    @Column(name = "discount") // Aseguramos que coincida con el SQL
    private int discount;
    
    @Column(name = "discount_price") // Aseguramos que coincida con el SQL
    private double discountPrice;
    
    @Column(name = "product_category") // Este campo existe en la tabla SQL
    private String productCategory;
    
    @Column(name = "product_image") // Aseguramos que coincida con el SQL
    private String productImage;
    
    @Column(name = "is_active") // Aseguramos que coincida con el SQL
    private Boolean isActive;
    
    @Column(name = "created_at") // Aseguramos que coincida con el SQL
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at") // Aseguramos que coincida con el SQL
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> carts;
    
    // Constructores
    public Product() {
    }
    
    public Product(Long id, String productTitle, String productDescription, double productPrice,
            int productStock, int discount, double discountPrice, String productCategory,
            String productImage, Boolean isActive, LocalDateTime createdAt,
            LocalDateTime updatedAt, Category category, List<Cart> carts) {
        this.id = id;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.discount = discount;
        this.discountPrice = discountPrice;
        this.productCategory = productCategory;
        this.productImage = productImage;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
        this.carts = carts;
    }
    
    // Getters y setters sin cambios
    public Long getId() {
        return id;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public double getProductPrice() {
        return productPrice;
    }
    
    public int getProductStock() {
        return productStock;
    }
    
    public int getDiscount() {
        return discount;
    }
    
    public double getDiscountPrice() {
        return discountPrice;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public String getProductImage() {
        return productImage;
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
    
    public Category getCategory() {
        return category;
    }
    
    public List<Cart> getCarts() {
        return carts;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    
    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }
    
    public void setDiscount(int discount) {
        this.discount = discount;
    }
    
    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage;
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
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
    
    // Método para calcular el precio con descuento
    @Transient
    public void calculateDiscountPrice() {
        if (this.discount > 0) {
            this.discountPrice = this.productPrice - (this.productPrice * this.discount / 100);
        } else {
            this.discountPrice = this.productPrice;
        }
    }
}