package com.mdtalalwasim.ecommerce.utils;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mdtalalwasim.ecommerce.entity.OrderAddress;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.User;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class InvoicePDFGenerator {

    public static byte[] generateInvoicePDF(User user, OrderAddress address, List<ProductOrder> orders, double subtotal, double shipping, double tax, double total) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Estilos
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        // Encabezado
        Paragraph title = new Paragraph("DECORANDO - FACTURA DE COMPRA", headerFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
        document.add(new Paragraph("Cliente: " + user.getFullName()));
        document.add(new Paragraph("Email: " + user.getEmail()));
        document.add(new Paragraph("Teléfono: " + address.getMobile()));
        document.add(new Paragraph("Dirección: " + address.getAddress() + ", " + address.getCity() + " - " + address.getState()));
        document.add(Chunk.NEWLINE);

        // Tabla de productos
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        addTableHeader(table, Stream.of("Producto", "Cantidad", "Precio unitario", "Total"));

        for (ProductOrder order : orders) {
            table.addCell(new PdfPCell(new Phrase(order.getProduct().getProductTitle())));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(order.getQuantity()))));
            table.addCell(new PdfPCell(new Phrase("$" + format(order.getPrice() / order.getQuantity()))));
            table.addCell(new PdfPCell(new Phrase("$" + format(order.getPrice()))));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Totales
        Paragraph subtotalLine = new Paragraph("Subtotal: $" + format(subtotal), labelFont);
        Paragraph shippingLine = new Paragraph("Envío: $" + format(shipping), labelFont);
        Paragraph taxLine = new Paragraph("Impuestos: $" + format(tax), labelFont);
        Paragraph totalLine = new Paragraph("TOTAL: $" + format(total), headerFont);

        subtotalLine.setAlignment(Element.ALIGN_RIGHT);
        shippingLine.setAlignment(Element.ALIGN_RIGHT);
        taxLine.setAlignment(Element.ALIGN_RIGHT);
        totalLine.setAlignment(Element.ALIGN_RIGHT);

        document.add(subtotalLine);
        document.add(shippingLine);
        document.add(taxLine);
        document.add(totalLine);

        document.close();
        return out.toByteArray();
    }

    private static void addTableHeader(PdfPTable table, Stream<String> headers) {
        headers.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }

    private static String format(double amount) {
        return String.format("%,.0f", amount); // Formato con separadores de miles
    }
}
