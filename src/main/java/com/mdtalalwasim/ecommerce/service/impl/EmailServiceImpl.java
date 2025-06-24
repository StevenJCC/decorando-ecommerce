package com.mdtalalwasim.ecommerce.service.impl;

import com.mdtalalwasim.ecommerce.service.EmailService;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void enviarCorreoRegistro(String destinatario, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Bienvenido a Decorando");
        mensaje.setText("Hola " + nombre + ", gracias por registrarte en nuestra tienda.");
        mailSender.send(mensaje);
    }

    @Override
    public void enviarCorreoPedido(String destinatario, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Confirmación de Pedido");
        mensaje.setText("Hola " + nombre + ", tu pedido ha sido recibido exitosamente.");
        mailSender.send(mensaje);
    }

    @Override
    public void sendBulkEmail(List<String> recipients, String subject, String body, MultipartFile attachment) {
        for (String recipient : recipients) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("decorando976@gmail.com");
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }
    }

    @Override
    public void sendInvoiceEmailWithAttachment(String to, String subject, String htmlBody, byte[] pdfBytes, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        DataSource dataSource = new DataSource() {
            @Override
            public java.io.InputStream getInputStream() {
                return new ByteArrayInputStream(pdfBytes);
            }

            @Override
            public java.io.OutputStream getOutputStream() {
                throw new UnsupportedOperationException("Not supported");
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public String getName() {
                return fileName;
            }
        };

        helper.addAttachment(fileName, dataSource);
        mailSender.send(message);
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(to);
        mensaje.setSubject(subject);
        mensaje.setText(text);
        mailSender.send(mensaje);
    }
}
