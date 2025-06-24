package com.mdtalalwasim.ecommerce.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/advanced-inventory-report")
public class AdvancedInventoryReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<InputStreamResource> generateAdvancedReport() {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph header = new Paragraph("Reporte Completo de Inventario", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph("Fecha de Generación: " + new Date()));
            document.add(Chunk.NEWLINE);

            // Tabla de productos
            String sqlProducts = "SELECT id, product_title, product_category, product_stock, product_price FROM product";
            List<Map<String, Object>> products = jdbcTemplate.queryForList(sqlProducts);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.addCell(createHeaderCell("ID"));
            table.addCell(createHeaderCell("Producto"));
            table.addCell(createHeaderCell("Categoría"));
            table.addCell(createHeaderCell("Stock"));
            table.addCell(createHeaderCell("Precio"));

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (Map<String, Object> row : products) {
                table.addCell(row.get("id").toString());
                table.addCell(row.get("product_title").toString());
                table.addCell(row.get("product_category").toString());
                table.addCell(row.get("product_stock").toString());
                table.addCell("$" + row.get("product_price").toString());

                dataset.addValue(Integer.parseInt(row.get("product_stock").toString()), "Stock", row.get("product_title").toString());
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Histórico de entradas y salidas
            Font movementFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("Histórico de Movimientos de Inventario", movementFont));
            document.add(Chunk.NEWLINE);

            String sqlMovements = "SELECT product_id, movement_type, quantity, movement_date FROM inventory_movement ORDER BY movement_date DESC";
            List<Map<String, Object>> movements = jdbcTemplate.queryForList(sqlMovements);

            PdfPTable moveTable = new PdfPTable(4);
            moveTable.setWidthPercentage(100);
            moveTable.addCell(createHeaderCell("Producto ID"));
            moveTable.addCell(createHeaderCell("Tipo"));
            moveTable.addCell(createHeaderCell("Cantidad"));
            moveTable.addCell(createHeaderCell("Fecha"));

            for (Map<String, Object> movement : movements) {
                moveTable.addCell(movement.get("product_id").toString());
                moveTable.addCell(movement.get("movement_type").toString());
                moveTable.addCell(movement.get("quantity").toString());
                moveTable.addCell(movement.get("movement_date").toString());
            }
            document.add(moveTable);
            document.add(Chunk.NEWLINE);

            // Gráfico profesional
            JFreeChart chart = ChartFactory.createBarChart(
                    "Stock Actual por Producto",
                    "Producto",
                    "Cantidad",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );

            chart.setBackgroundPaint(Color.WHITE);
            chart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);
            BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
            renderer.setSeriesPaint(0, new Color(79, 129, 189));

            BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
            Image chartImage = Image.getInstance(bufferedImage, null);
            chartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(chartImage);

            document.add(Chunk.NEWLINE);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10);
            Paragraph footer = new Paragraph("Generado automáticamente - DECORANDO - " + new Date(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=inventory-full-report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
    }

    private PdfPCell createHeaderCell(String content) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBackgroundColor(new BaseColor(0, 102, 204));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
