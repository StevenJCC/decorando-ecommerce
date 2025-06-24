package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.DeliveryAssignment;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.DeliveryAssignmentRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private DeliveryAssignmentRepository deliveryAssignmentRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String showOrderList(@RequestParam(name = "status", required = false) String status, Model model) {
        List<ProductOrder> orders;

        if (status != null && !status.isEmpty()) {
            orders = productOrderRepository.findByStatus(status);
        } else {
            orders = productOrderRepository.findAll();
        }

        for (ProductOrder order : orders) {
            Optional<DeliveryAssignment> optional = deliveryAssignmentRepository.findByOrder(order);
            optional.ifPresent(order::setDeliveryAssignment);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        return "admin/orders/order-home";
    }

    @PostMapping("/assign-delivery")
    public String assignDelivery(@RequestParam("orderId") Long orderId,
                                 @RequestParam("deliveryFirstName") String firstName,
                                 @RequestParam("deliveryLastName") String lastName,
                                 @RequestParam("deliveryPhone") String deliveryPhone,
                                 @RequestParam("deliveryApp") String deliveryApp,
                                 @RequestParam("deliveryStatus") String deliveryStatus,
                                 HttpSession session) {

        ProductOrder order = productOrderRepository.findById(orderId).orElse(null);
        if (order == null) {
            session.setAttribute("errorMsg", "Orden no encontrada");
            return "redirect:/admin/orders";
        }

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setDeliveryFirstName(firstName);
        assignment.setDeliveryLastName(lastName);
        assignment.setDeliveryPhone(deliveryPhone);
        assignment.setDeliveryApp(deliveryApp);
        assignment.setDeliveryStatus(deliveryStatus);

        deliveryAssignmentRepository.save(assignment);

        session.setAttribute("successMsg", "Repartidor asignado correctamente.");
        return "redirect:/admin/orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam("id") Long orderId,
                                    @RequestParam("status") String status,
                                    HttpSession session) {
        Optional<ProductOrder> optional = productOrderRepository.findById(orderId);
        if (optional.isPresent()) {
            ProductOrder order = optional.get();
            order.setStatus(status);
            productOrderRepository.save(order);
            session.setAttribute("successMsg", "Estado actualizado a: " + status);
        } else {
            session.setAttribute("errorMsg", "Pedido no encontrado");
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/update-delivery-status")
    public String updateDeliveryStatus(@RequestParam("assignmentId") Long assignmentId,
                                       @RequestParam("newStatus") String newStatus,
                                       HttpSession session) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId).orElse(null);

        if (assignment != null) {
            assignment.setDeliveryStatus(newStatus);
            deliveryAssignmentRepository.save(assignment);

            if ("ENTREGADO".equalsIgnoreCase(newStatus)) {
                ProductOrder order = assignment.getOrder();
                order.setStatus("ENTREGADO");
                productOrderRepository.save(order);

                User user = order.getUser();
                String subject = "¡Tu pedido ha sido entregado!";
                String message = "Hola " + user.getFullName() + ",\n\n" +
                        "Nos complace informarte que tu pedido con número " + order.getOrderId() + " ha sido entregado exitosamente.\n\n" +
                        "Gracias por comprar en Decorando.\n\n" +
                        "¡Esperamos verte pronto!";

                emailService.sendSimpleMessage(user.getEmail(), subject, message);
            }

            session.setAttribute("successMsg", "Estado de entrega actualizado.");
        } else {
            session.setAttribute("errorMsg", "No se encontró el reparto.");
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/asignar-repartidor")
    public String mostrarFormularioAsignacion(@RequestParam("orderId") Long orderId, Model model, HttpSession session) {
        Optional<ProductOrder> optionalOrder = productOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            session.setAttribute("errorMsg", "No se encontró el pedido");
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", optionalOrder.get());
        return "admin/orders/asignar-repartidor";
    }

    @PostMapping("/asignar-repartidor")
    public String procesarAsignacionRepartidor(@RequestParam("orderId") Long orderId,
                                               @RequestParam("deliveryFirstName") String firstName,
                                               @RequestParam("deliveryLastName") String lastName,
                                               @RequestParam("deliveryPhone") String phone,
                                               @RequestParam("deliveryApp") String app,
                                               @RequestParam("deliveryStatus") String status,
                                               HttpSession session) {
        Optional<ProductOrder> optionalOrder = productOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            session.setAttribute("errorMsg", "Pedido no encontrado");
            return "redirect:/admin/orders";
        }

        ProductOrder order = optionalOrder.get();
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setDeliveryFirstName(firstName);
        assignment.setDeliveryLastName(lastName);
        assignment.setDeliveryPhone(phone);
        assignment.setDeliveryApp(app);
        assignment.setDeliveryStatus(status);

        deliveryAssignmentRepository.save(assignment);

        if ("ENTREGADO".equalsIgnoreCase(status)) {
            order.setStatus("ENTREGADO");
            productOrderRepository.save(order);
        }

        session.setAttribute("successMsg", "Repartidor asignado correctamente.");
        return "redirect:/admin/orders";
    }

}
