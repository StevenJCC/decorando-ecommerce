package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.dto.MovementCountDto;
import com.mdtalalwasim.ecommerce.dto.ProductSalesDto;

import java.awt.image.BufferedImage;
import java.util.List;

public interface AdminStatsService {
    List<ProductSalesDto> getTopSellingProducts(int limit);
    List<MovementCountDto> getMovementCounts();

    // 👇 MÉTODOS NUEVOS
    BufferedImage getTopSellingChart();
    BufferedImage getMovementTypeChart();
}
