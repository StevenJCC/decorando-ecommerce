package com.mdtalalwasim.ecommerce.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CurtainReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/simple-pdf-report")
    public ResponseEntity<InputStreamResource> generateSimplePdfReport() {
        try {
            // Consulta directa a la base de datos
            String sql = "SELECT id, product_title, product_stock, product_price " +
                         "FROM product " +
                         "WHERE product_title LIKE '%cortina%' " +
                         "ORDER BY product_title";
            
            List<Map<String, Object>> curtains = jdbcTemplate.queryForList(sql);
            
            // Generar PDF
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Reporte de Inventario de Cortinas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Fecha
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY);
            Paragraph datePara = new Paragraph("Fecha: " + new Date(), dateFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            document.add(datePara);
            
            document.add(Chunk.NEWLINE);

            // Tabla
            PdfPTable table = new PdfPTable(4); // ID, Nombre, Stock, Precio
            table.setWidthPercentage(100);
            
            // Encabezados
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            
            String[] tableHeaders = {"ID", "Producto", "Stock", "Precio"};
            for (String header : tableHeaders) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(new BaseColor(41, 128, 185));
                headerCell.setPadding(5);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }
            
            // Datos
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            boolean shaded = false;
            int totalStock = 0;
            double totalValue = 0.0;
            
            for (Map<String, Object> curtain : curtains) {
                // ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(curtain.get("id")), contentFont));
                if (shaded) idCell.setBackgroundColor(new BaseColor(240, 240, 240));
                table.addCell(idCell);
                
                // Nombre
                PdfPCell nameCell = new PdfPCell(new Phrase(String.valueOf(curtain.get("product_title")), contentFont));
                if (shaded) nameCell.setBackgroundColor(new BaseColor(240, 240, 240));
                table.addCell(nameCell);
                
                // Stock
                int stock = 0;
                if (curtain.get("product_stock") != null) {
                    stock = ((Number) curtain.get("product_stock")).intValue();
                }
                PdfPCell stockCell = new PdfPCell(new Phrase(String.valueOf(stock), contentFont));
                if (shaded) stockCell.setBackgroundColor(new BaseColor(240, 240, 240));
                stockCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(stockCell);
                
                // Precio
                double price = 0.0;
                if (curtain.get("product_price") != null) {
                    price = ((Number) curtain.get("product_price")).doubleValue();
                }
                PdfPCell priceCell = new PdfPCell(new Phrase("$" + String.format("%.2f", price), contentFont));
                if (shaded) priceCell.setBackgroundColor(new BaseColor(240, 240, 240));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);
                
                // Alternar sombreado
                shaded = !shaded;
                
                // Sumar totales
                totalStock += stock;
                totalValue += (stock * price);
            }
            
            document.add(table);
            
            // Resumen
            document.add(Chunk.NEWLINE);
            Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
            
            Paragraph summaryStock = new Paragraph("Total de Cortinas en Inventario: " + totalStock + " unidades", summaryFont);
            summaryStock.setAlignment(Element.ALIGN_RIGHT);
            document.add(summaryStock);
            
            Paragraph summaryValue = new Paragraph("Valor Total del Inventario: $" + String.format("%.2f", totalValue), summaryFont);
            summaryValue.setAlignment(Element.ALIGN_RIGHT);
            document.add(summaryValue);
            
            // Pie de página
            document.add(Chunk.NEWLINE);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
            Paragraph footer = new Paragraph("© DECORANDO - Este reporte fue generado el " + 
                                            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), 
                                            footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
            
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "inline; filename=inventario_cortinas.pdf");

            return ResponseEntity
                    .ok()
                    .headers(httpHeaders)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }
}