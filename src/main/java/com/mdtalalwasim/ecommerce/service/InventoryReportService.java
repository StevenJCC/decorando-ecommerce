package com.mdtalalwasim.ecommerce.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class InventoryReportService {

    @Autowired
    private ProductRepository productRepository;

    public byte[] generateInventoryReportWithChart() throws Exception {
        List<Product> products = productRepository.findAll();

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // TÍTULO
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
        Paragraph title = new Paragraph("Reporte Completo de Inventario", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Tabla de datos
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 4, 2, 2, 2});
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        String[] headers = {"ID", "Producto", "Categoría", "Stock", "Precio"};
        for (String header : headers) {
            PdfPCell hcell = new PdfPCell(new Phrase(header, headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(hcell);
        }

        // Datos
        for (Product product : products) {
            table.addCell(String.valueOf(product.getId()));
            table.addCell(product.getProductTitle());
            table.addCell(product.getProductCategory());
            table.addCell(String.valueOf(product.getProductStock()));
            table.addCell("$" + product.getProductPrice());
        }

        document.add(table);

        // Generar gráfica
        document.add(new Paragraph("\n\nStock Visual (Gráfico de Barras)", headFont));
        Image chartImage = generateStockChart(products);
        document.add(chartImage);

        document.close();
        writer.close();

        return out.toByteArray();
    }

    private Image generateStockChart(List<Product> products) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Product product : products) {
            dataset.addValue(product.getProductStock(), "Stock", product.getProductTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Stock de Productos",
                "Producto",
                "Unidades",
                dataset
        );

        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        EncoderUtil.writeBufferedImage(chart.createBufferedImage(800, 400), "png", chartOut);

        Image chartImage = Image.getInstance(chartOut.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scalePercent(70);
        return chartImage;
    }
}
