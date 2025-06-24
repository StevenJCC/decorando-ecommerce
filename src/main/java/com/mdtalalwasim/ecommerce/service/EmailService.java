package com.mdtalalwasim.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface EmailService {
    void enviarCorreoRegistro(String destinatario, String nombre);
    void enviarCorreoPedido(String destinatario, String nombre);
    void sendBulkEmail(List<String> recipients, String subject, String body, MultipartFile attachment);
    void sendInvoiceEmailWithAttachment(String to, String subject, String htmlBody, byte[] pdfBytes, String fileName) throws Exception;
    void sendSimpleMessage(String to, String subject, String text);
}
