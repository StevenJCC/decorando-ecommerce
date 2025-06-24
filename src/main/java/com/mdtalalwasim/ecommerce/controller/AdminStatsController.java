package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.dto.MovementCountDto;
import com.mdtalalwasim.ecommerce.dto.ProductSalesDto;
import com.mdtalalwasim.ecommerce.service.InventoryMovementService;
import com.mdtalalwasim.ecommerce.service.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/stats")
public class AdminStatsController {

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    /**
     * Productos más vendidos (top N).
     * Ejemplo: GET /admin/stats/top-products?limit=5
     */
    @GetMapping("/top-products")
    public List<ProductSalesDto> topProducts(@RequestParam(defaultValue = "5") int limit) {
        return productOrderService.getTopSellingProducts(limit);
    }

    /**
     * Conteo de movimientos de inventario agrupados por tipo.
     * GET /admin/stats/movement-counts
     */
    @GetMapping("/movement-counts")
    public List<MovementCountDto> movementCounts() {
        return inventoryMovementService.getMovementCounts();
    }
}
