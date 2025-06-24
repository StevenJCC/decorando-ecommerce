package com.mdtalalwasim.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mdtalalwasim.ecommerce.service.EmailService;
import com.mdtalalwasim.ecommerce.service.UserService;

import java.util.List;

@Controller
public class EmailPromotionController {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/admin/email-promotions")
    public String showEmailPromotionForm(Model model) {
        return "admin/email-promotions";
    }

    @PostMapping("/admin/send-promotion-email")
    public String sendPromotionEmail(
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @RequestParam(value = "userType", required = false) String userType,
            RedirectAttributes redirectAttributes) {
        
        try {
            List<String> recipients = userService.getUserEmailsByType(userType);
            
            if (recipients.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No hay destinatarios para enviar el correo.");
                return "redirect:/admin/email-promotions";
            }
            
            emailService.sendBulkEmail(recipients, subject, message, attachment);
            
            redirectAttributes.addFlashAttribute("success", 
                    "Correos enviados exitosamente a " + recipients.size() + " usuarios.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                    "Error al enviar los correos: " + e.getMessage());
        }
        
        return "redirect:/admin/email-promotions";
    }
}