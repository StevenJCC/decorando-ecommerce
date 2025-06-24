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
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
public class SimplePDFReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/simple-curtains")
    @ResponseBody
    public ResponseEntity<InputStreamResource> generateSimpleCurtainsReport() {
        try {
            System.out.println("Entrando al método generateSimpleCurtainsReport");
            
            // Consulta SQL adaptada a la estructura real de la base de datos
            // Como no hay relación directa entre product y category, usamos LIKE en product_title
            String sql = "SELECT id, product_title, product_stock, product_category, product_price " +
                         "FROM product " +
                         "WHERE LOWER(product_title) LIKE '%cortina%' " +
                         "ORDER BY product_category, product_title";
            
            List<Map<String, Object>> curtains = jdbcTemplate.queryForList(sql);
            System.out.println("Consulta ejecutada. Número de resultados: " + curtains.size());
            
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
            PdfPTable table = new PdfPTable(5); // ID, Nombre, Stock, Categoría, Precio
            table.setWidthPercentage(100);
            
            // Encabezados
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            
            String[] tableHeaders = {"ID", "Producto", "Stock", "Categoría", "Precio"};
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
                String stock = curtain.get("product_stock") != null ? String.valueOf(curtain.get("product_stock")) : "0";
                PdfPCell stockCell = new PdfPCell(new Phrase(stock, contentFont));
                if (shaded) stockCell.setBackgroundColor(new BaseColor(240, 240, 240));
                stockCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(stockCell);
                
                // Categoría
                String category = curtain.get("product_category") != null ? 
                    String.valueOf(curtain.get("product_category")) : "Sin categoría";
                PdfPCell categoryCell = new PdfPCell(new Phrase(category, contentFont));
                if (shaded) categoryCell.setBackgroundColor(new BaseColor(240, 240, 240));
                table.addCell(categoryCell);
                
                // Precio
                String price = curtain.get("product_price") != null ? 
                    "$" + String.format("%.2f", curtain.get("product_price")) : "N/A";
                PdfPCell priceCell = new PdfPCell(new Phrase(price, contentFont));
                if (shaded) priceCell.setBackgroundColor(new BaseColor(240, 240, 240));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);
                
                // Alternar sombreado
                shaded = !shaded;
                
                // Sumar stock y valor
                try {
                    int stockValue = Integer.parseInt(stock);
                    totalStock += stockValue;
                    
                    if (curtain.get("product_price") != null) {
                        double priceValue = Double.parseDouble(curtain.get("product_price").toString());
                        totalValue += stockValue * priceValue;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar si no es un número
                }
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
            System.out.println("PDF generado correctamente");
            
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
            System.err.println("Error al generar el reporte: " + e.getMessage());
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }
}