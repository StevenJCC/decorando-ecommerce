package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    @Autowired
    private CategoryService categoryService;

    /**
     * Parsea un archivo CSV a una lista de productos
     */
    public List<Product> parseProductsCsv(MultipartFile file) throws Exception {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Configuración de OpenCSV para el mapeo de columnas a propiedades
            HeaderColumnNameMappingStrategy<ProductCsvDto> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ProductCsvDto.class);

            // Crear el parser CSV
            CsvToBean<ProductCsvDto> csvToBean = new CsvToBeanBuilder<ProductCsvDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // Parsear el CSV
            List<ProductCsvDto> productDtos = csvToBean.parse();
            return convertToProducts(productDtos);
        } catch (Exception e) {
            throw new Exception("Error al parsear el archivo CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Parsea un archivo CSV a una lista de categorías
     */
    public List<Category> parseCategoriesCsv(MultipartFile file) throws Exception {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Configuración de OpenCSV para el mapeo de columnas a propiedades
            HeaderColumnNameMappingStrategy<CategoryCsvDto> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(CategoryCsvDto.class);

            // Crear el parser CSV
            CsvToBean<CategoryCsvDto> csvToBean = new CsvToBeanBuilder<CategoryCsvDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // Parsear el CSV
            List<CategoryCsvDto> categoryDtos = csvToBean.parse();
            return convertToCategories(categoryDtos);
        } catch (Exception e) {
            throw new Exception("Error al parsear el archivo CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte los DTOs de CSV a entidades de productos
     */
    private List<Product> convertToProducts(List<ProductCsvDto> dtos) throws Exception {
        List<Product> products = new ArrayList<>();
        
        for (ProductCsvDto dto : dtos) {
            Product product = new Product();
            product.setProductTitle(dto.getProductTitle());
            product.setProductDescription(dto.getProductDescription());
            product.setProductPrice(Double.parseDouble(dto.getProductPrice()));
            
            // Categoría como string
            product.setProductCategory(dto.getProductCategory());
            
            // Stock de producto
            product.setProductStock(Integer.parseInt(dto.getProductStock()));
            
            // Descuento
            product.setDiscount(Integer.parseInt(dto.getDiscount()));
            
            // Precio con descuento (calculado automáticamente)
            Double originalPrice = Double.parseDouble(dto.getProductPrice());
            Integer discountPercentage = Integer.parseInt(dto.getDiscount());
            Double discountedPrice = originalPrice - (originalPrice * discountPercentage / 100.0);
            product.setDiscountPrice(discountedPrice);
            
            // Estado activo
            product.setIsActive(Boolean.parseBoolean(dto.getIsActive()));
            
            // Imagen del producto
            if (dto.getProductImage() != null && !dto.getProductImage().isEmpty()) {
                product.setProductImage(dto.getProductImage());
            } else {
                product.setProductImage("default.jpg");
            }
            
            products.add(product);
        }
        
        return products;
    }
    
    /**
     * Convierte los DTOs de CSV a entidades de categorías
     */
    private List<Category> convertToCategories(List<CategoryCsvDto> dtos) {
        List<Category> categories = new ArrayList<>();
        
        for (CategoryCsvDto dto : dtos) {
            Category category = new Category();
            category.setCategoryName(dto.getCategoryName());
            
            // Imagen de la categoría
            if (dto.getCategoryImage() != null && !dto.getCategoryImage().isEmpty()) {
                category.setCategoryImage(dto.getCategoryImage());
            } else {
                category.setCategoryImage("default-category.jpg");
            }
            
            category.setIsActive(Boolean.parseBoolean(dto.getIsActive()));
            categories.add(category);
        }
        
        return categories;
    }
    
    // Clases DTO para el mapeo CSV
    
    /**
     * Clase DTO para mapear los campos CSV de productos
     */
    public static class ProductCsvDto {
        private String productTitle;
        private String productDescription;
        private String productCategory;
        private String productPrice;
        private String productStock;
        private String discount;
        private String isActive;
        private String productImage;

        // Getters y setters
        public String getProductTitle() { return productTitle; }
        public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
        
        public String getProductDescription() { return productDescription; }
        public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
        
        public String getProductCategory() { return productCategory; }
        public void setProductCategory(String productCategory) { this.productCategory = productCategory; }
        
        public String getProductPrice() { return productPrice; }
        public void setProductPrice(String productPrice) { this.productPrice = productPrice; }
        
        public String getProductStock() { return productStock; }
        public void setProductStock(String productStock) { this.productStock = productStock; }
        
        public String getDiscount() { return discount; }
        public void setDiscount(String discount) { this.discount = discount; }
        
        public String getIsActive() { return isActive; }
        public void setIsActive(String isActive) { this.isActive = isActive; }
        
        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }
    }
    
    /**
     * Clase DTO para mapear los campos CSV de categorías
     */
    public static class CategoryCsvDto {
        private String categoryName;
        private String categoryImage;
        private String isActive;

        // Getters y setters
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public String getCategoryImage() { return categoryImage; }
        public void setCategoryImage(String categoryImage) { this.categoryImage = categoryImage; }
        
        public String getIsActive() { return isActive; }
        public void setIsActive(String isActive) { this.isActive = isActive; }
    }
}