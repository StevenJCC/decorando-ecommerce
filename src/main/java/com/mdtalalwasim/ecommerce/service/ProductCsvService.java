package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProductCsvService {

    private static final Logger logger = Logger.getLogger(ProductCsvService.class.getName());

    @Autowired
    private ProductRepository productRepository;

    /**
     * Procesa los productos y guarda los productos en la base de datos
     * 
     * @param products Lista de productos para ser procesados
     * @return Número de productos procesados exitosamente
     */
    public int processProducts(List<Product> products) {
        int successCount = 0;
        int errorCount = 0;

        // Procesamos cada producto
        for (Product product : products) {
            try {
                validateProduct(product);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                // Guardamos el producto en la base de datos
                productRepository.save(product);
                successCount++;
            } catch (Exception e) {
                logger.warning("Error procesando el producto: " + e.getMessage());
                errorCount++;
            }
        }

        logger.info("Procesamiento de productos completado. Éxitos: " + successCount + ", Errores: " + errorCount);
        return successCount;
    }

    /**
     * Valida los datos de un producto
     */
    private void validateProduct(Product product) {
        if (product.getProductTitle() == null || product.getProductTitle().isEmpty()) {
            throw new IllegalArgumentException("El título del producto es obligatorio");
        }

        if (product.getProductDescription() == null || product.getProductDescription().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria");
        }

        if (product.getProductPrice() <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor que cero");
        }
    }
}
