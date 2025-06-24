package com.mdtalalwasim.ecommerce.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

@Service
public class SalesReportService {

    public ByteArrayOutputStream generateSalesReport(List<ProductOrder> orders, BufferedImage chart1, BufferedImage chart2) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Encabezado
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.WHITE);
        Paragraph header = new Paragraph("Reporte de Ventas - Decorando", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        PdfPCell headerCell = new PdfPCell(header);
        headerCell.setBackgroundColor(new BaseColor(63, 81, 181));
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPadding(10);

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.addCell(headerCell);
        document.add(headerTable);
        document.add(Chunk.NEWLINE);

        // Insertar gráficas
// Insertar gráficas
if (chart1 != null) {
    Image chartImage1 = Image.getInstance(ImageIOHelper.toBytes(chart1));
    chartImage1.scaleToFit(500, 300);
    chartImage1.setAlignment(Image.ALIGN_CENTER);
    document.add(chartImage1);
}

document.add(Chunk.NEWLINE);

if (chart2 != null) {
    Image chartImage2 = Image.getInstance(ImageIOHelper.toBytes(chart2));
    chartImage2.scaleToFit(500, 300);
    chartImage2.setAlignment(Image.ALIGN_CENTER);
    document.add(chartImage2);
}

        document.add(Chunk.NEWLINE);

        // Tabla resumen de ventas
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font tableBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Paragraph tableTitle = new Paragraph("Resumen de Ventas", tableHeaderFont);
        tableTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(tableTitle);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 3f, 3f, 2f, 2f});

        table.addCell(new PdfPCell(new Phrase("Producto", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Cliente", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Fecha", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Cantidad", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Total", tableHeaderFont)));

        for (ProductOrder order : orders) {
            table.addCell(new PdfPCell(new Phrase(order.getProduct().getProductTitle(), tableBodyFont)));
            table.addCell(new PdfPCell(new Phrase(order.getUser().getFullName(), tableBodyFont)));
            table.addCell(new PdfPCell(new Phrase(dateFormat.format(order.getOrderDate()), tableBodyFont)));
            table.addCell(new PdfPCell(new Phrase(order.getQuantity().toString(), tableBodyFont)));
            table.addCell(new PdfPCell(new Phrase(currencyFormat.format(order.getPrice()), tableBodyFont)));
        }

        document.add(table);
        document.close();
        return out;
    }

    // Clase auxiliar para convertir BufferedImage a byte[]
    private static class ImageIOHelper {
        public static byte[] toBytes(BufferedImage image) throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }
}
