// src/main/java/com/mdtalalwasim/ecommerce/dto/ProductSalesDto.java
package com.mdtalalwasim.ecommerce.dto;

public class ProductSalesDto {
    private Long productId;
    private String productTitle;
    private Long totalSold;

    public ProductSalesDto(Long productId, String productTitle, Long totalSold) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.totalSold = totalSold;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }
}
