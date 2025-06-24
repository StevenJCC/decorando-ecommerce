package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.InventoryMovement;
import com.mdtalalwasim.ecommerce.entity.MovementType;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.InventoryMovementService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController {

    @Autowired private InventoryMovementService inventoryMovementService;
    @Autowired private ProductService productService;
    @Autowired private UserService userService;

    // Mostrar nombre y correo del usuario en sesión
    @ModelAttribute
    public void addUserDetailsToModel(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("currentLoggedInUserDetails", user);
        }
    }

    @ModelAttribute("movementTypeLabels")
    public Map<MovementType, String> movementTypeLabels() {
        Map<MovementType, String> labels = new LinkedHashMap<>();
        labels.put(MovementType.IN, "Entrada");
        labels.put(MovementType.OUT, "Salida");
        labels.put(MovementType.RETURN, "Devolución");
        labels.put(MovementType.DAMAGED, "Producto dañado");
        return labels;
    }

    @GetMapping
    public String inventoryHome(
            @RequestParam Optional<MovementType> type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> to,
            Model model) {

        List<InventoryMovement> movements = inventoryMovementService.findMovements(
                type.orElse(null),
                from.orElse(null),
                to.orElse(null)
        );

        model.addAttribute("movements", movements);
        model.addAttribute("newMovement", new InventoryMovement());
        model.addAttribute("selectedType", type.orElse(null));
        model.addAttribute("from", from.orElse(null));
        model.addAttribute("to", to.orElse(null));

        return "admin/inventory/inventory-home";
    }

    @PostMapping("/add")
    public String addMovement(@ModelAttribute InventoryMovement movement,
                              RedirectAttributes redirectAttributes) {
        movement.setMovementDate(LocalDateTime.now());
        inventoryMovementService.saveMovement(movement);

        Product product = productService.getProductById(movement.getProductId());
        if (product != null) {
            int stock = product.getProductStock();
            MovementType mt = movement.getMovementType();

            if (mt == MovementType.IN || mt == MovementType.RETURN) {
                stock += movement.getQuantity();
            } else if (mt == MovementType.OUT || mt == MovementType.DAMAGED) {
                stock -= movement.getQuantity();
            }

            product.setProductStock(Math.max(stock, 0));
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Movimiento registrado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
        }

        return "redirect:/admin/inventory";
    }
}
