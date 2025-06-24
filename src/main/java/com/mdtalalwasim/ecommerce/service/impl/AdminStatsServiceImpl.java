package com.mdtalalwasim.ecommerce.service.impl;

import com.mdtalalwasim.ecommerce.dto.MovementCountDto;
import com.mdtalalwasim.ecommerce.dto.ProductSalesDto;
import com.mdtalalwasim.ecommerce.repository.InventoryMovementRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.service.AdminStatsService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Override
    public List<ProductSalesDto> getTopSellingProducts(int limit) {
        return productOrderRepository.findTopSelling(limit);
    }

    @Override
    public List<MovementCountDto> getMovementCounts() {
        return inventoryMovementRepository.countMovementsByType();
    }

    @Override
    public BufferedImage getTopSellingChart() {
        List<ProductSalesDto> topProducts = getTopSellingProducts(5);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ProductSalesDto dto : topProducts) {
            dataset.addValue(dto.getTotalSold(), "Unidades vendidas", dto.getProductTitle());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Productos más vendidos",
                "Producto",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        return chart.createBufferedImage(500, 300);
    }

    @Override
    public BufferedImage getMovementTypeChart() {
        List<MovementCountDto> movementCounts = getMovementCounts();
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (MovementCountDto mc : movementCounts) {
            dataset.setValue(mc.getType().toString(), mc.getCount());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Movimientos por tipo",
                dataset,
                true,
                true,
                false
        );

        return chart.createBufferedImage(500, 300);
    }
}
