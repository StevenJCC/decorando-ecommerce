// Ruta: src/main/java/com/mdtalalwasim/ecommerce/repository/ProductOrderRepository.java
package com.mdtalalwasim.ecommerce.repository;

import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.dto.ProductSalesDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    List<ProductOrder> findByUser(User user);
    List<ProductOrder> findByStatus(String status);

    /**
     * Devuelve los productos más vendidos, limitado por Pageable.
     * Construye ProductSalesDto(productId, productTitle, totalSold).
     */
    @Query("SELECT new com.mdtalalwasim.ecommerce.dto.ProductSalesDto(" +
           "o.product.id, o.product.productTitle, SUM(o.quantity)) " +
           "FROM ProductOrder o " +
           "GROUP BY o.product.id, o.product.productTitle " +
           "ORDER BY SUM(o.quantity) DESC")
    List<ProductSalesDto> findTopSelling(Pageable pageable);

    /**
     * Conveniencia: top N productos más vendidos.
     */
    default List<ProductSalesDto> findTopSelling(int limit) {
        return findTopSelling(PageRequest.of(0, limit));
    }
}
