package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.service.SalesReportService;
import com.mdtalalwasim.ecommerce.service.AdminStatsService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class SalesReportController {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private SalesReportService salesReportService;

    @Autowired
    private AdminStatsService adminStatsService;

    /**
     * Ruta: /admin/sales-pdf
     * ✔ Muestra el PDF en el navegador con diseño moderno y gráfico.
     */
    @GetMapping("/sales-pdf")
    public void generateSalesReport(HttpServletResponse response) throws Exception {
        List<ProductOrder> orders = productOrderRepository.findAll();

        // Gráficos: productos más vendidos y movimientos por tipo
        BufferedImage chart1 = adminStatsService.getTopSellingChart();
        BufferedImage chart2 = adminStatsService.getMovementTypeChart();

        // Generar contenido del PDF
        ByteArrayOutputStream baos = salesReportService.generateSalesReport(orders, chart1, chart2);
        byte[] pdfContent = baos.toByteArray();

        // Mostrar PDF en el navegador
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_ventas.pdf");
        response.getOutputStream().write(pdfContent);
        response.getOutputStream().flush();
    }
}
