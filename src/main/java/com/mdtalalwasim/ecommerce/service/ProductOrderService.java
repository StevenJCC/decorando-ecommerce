package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.dto.ProductSalesDto;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.ProductOrderRequest;
import java.util.List;

public interface ProductOrderService {
    ProductOrder saveProductOrder(Long userId, ProductOrderRequest req);
    List<ProductOrder> getAllOrders();
    void updateOrderStatus(Long orderId, String status);

    /**
     * Devuelve los productos más vendidos (top N).
     * @param limit número máximo de registros a devolver
     */
    List<ProductSalesDto> getTopSellingProducts(int limit);
}
