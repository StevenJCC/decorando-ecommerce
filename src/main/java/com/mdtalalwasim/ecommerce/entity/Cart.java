package com.mdtalalwasim.ecommerce.entity;

import jakarta.persistence.*;
import java.text.NumberFormat;
import java.util.Locale;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private Double totalPrice;

    private Double totalOrderPrice;

    // --- Campos transitorios para mostrar precios formateados ---
    @Transient
    private String formattedPrice;

    @Transient
    private String formattedTotal;

    // --- Constructores ---
    public Cart() {
    }

    public Cart(Long id, User user, Product product, int quantity, Double totalPrice, Double totalOrderPrice) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.totalOrderPrice = totalOrderPrice;
    }

    // --- Getters y Setters estándar ---
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public Double getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalOrderPrice(Double totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    // --- Métodos para precios formateados ---
    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public String getFormattedTotal() {
        return formattedTotal;
    }

    public void setFormattedTotal(String formattedTotal) {
        this.formattedTotal = formattedTotal;
    }

    // --- Calcular el total del ítem ---
    public void calculateTotalPrice() {
        if (this.product != null && this.quantity > 0) {
            this.totalPrice = this.product.getDiscountPrice() * this.quantity;
        }
    }
}
