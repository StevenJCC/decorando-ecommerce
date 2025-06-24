package com.mdtalalwasim.ecommerce.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.OrderAddress;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.repository.OrderAddressRepository;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.EmailService;
import com.mdtalalwasim.ecommerce.service.UserService;
import com.mdtalalwasim.ecommerce.utils.InvoicePDFGenerator;
import java.text.NumberFormat;
import java.util.Locale;


import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderAddressRepository orderAddressRepository;
    @Autowired
    private EmailService emailService;


    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User currentUser = userService.getUserByEmail(email);
            model.addAttribute("currentLoggedInUserDetails", currentUser);
            Long totalQuantity = cartService.getTotalCartQuantity(currentUser.getId());
            model.addAttribute("countCartForUser", totalQuantity);
        }
        List<Category> categories = categoryService.findAllActiveCategory();
        model.addAttribute("allActiveCategory", categories);
    }

    @GetMapping("/")
    public String home() {
        return "user/user-home";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                             @RequestParam Long userId,
                             @RequestParam(defaultValue = "1") int quantity,
                             HttpSession session) {

        boolean stockDisponible = true;
        for (int i = 0; i < quantity; i++) {
            Cart cart = cartService.saveCart(productId, userId);
            if (ObjectUtils.isEmpty(cart)) {
                stockDisponible = false;
                break;
            }
        }

        if (stockDisponible) {
            session.setAttribute("successMsg", "Producto agregado al carrito.");
        } else {
            session.setAttribute("errorMsg", "Stock insuficiente para agregar esta cantidad.");
        }
        return "redirect:/products";
    }

@GetMapping("/cart")
public String loadCartPage(Principal principal, Model model, HttpSession session) {
    User user = getLoggedUserDetails(principal);
    List<Cart> carts = cartService.getCartsByUser(user.getId());

    double subtotal = 0.0;
    NumberFormat formatter = NumberFormat.getInstance(new Locale("es", "CO"));

    for (Cart cart : carts) {
        if (cart.getTotalPrice() == null || cart.getTotalPrice() == 0) {
            cart.calculateTotalPrice();
        }

        cart.setFormattedPrice(formatter.format(cart.getProduct().getDiscountPrice()));
        cart.setFormattedTotal(formatter.format(cart.getTotalPrice()));

        subtotal += cart.getTotalPrice();
    }

    double shippingCost = 10000; // estándar por defecto
    double tax = 0;
    double total = subtotal + shippingCost + tax;

    model.addAttribute("carts", carts);
    model.addAttribute("totalOrderPrice", total); // usado para resumen
    model.addAttribute("orderPrice", subtotal);
    model.addAttribute("shippingCost", shippingCost);
    model.addAttribute("tax", tax);

    model.addAttribute("orderPriceFormatted", formatter.format(subtotal));
    model.addAttribute("shippingCostFormatted", formatter.format(shippingCost));
    model.addAttribute("taxFormatted", formatter.format(tax));
    model.addAttribute("totalOrderPriceFormatted", formatter.format(total));

    session.removeAttribute("successMsg");
    session.removeAttribute("errorMsg");

    return "/user/cart";
}



    @GetMapping("/cart-quantity-update")
    public String updateCartQuantity(@RequestParam String symbol, @RequestParam Long cartId, HttpSession session) {
        boolean result = cartService.updateCartQuantity(symbol, cartId);
        if (!result && "increase".equalsIgnoreCase(symbol)) {
            session.setAttribute("errorMsg", "No se puede agregar más unidades: se alcanzó el límite del stock.");
        }
        return "redirect:/user/cart";
    }

    private User getLoggedUserDetails(Principal principal) {
        String email = principal.getName();
        return userService.getUserByEmail(email);
    }

@GetMapping("/orders")
public String orderPage(Principal principal, Model model) {
    User user = getLoggedUserDetails(principal);
    List<Cart> carts = cartService.getCartsByUser(user.getId());
    model.addAttribute("carts", carts);

   if (!carts.isEmpty()) {
    double subtotal = carts.stream().mapToDouble(Cart::getTotalPrice).sum();

    // Tarifa estándar por defecto (puedes ajustarla luego según selección)
    double shippingCost = 10000;
    double tax = 0;
    double total = subtotal + shippingCost + tax;

    // Formato colombiano
    NumberFormat formatter = NumberFormat.getInstance(new Locale("es", "CO"));

    model.addAttribute("orderPrice", subtotal);
    model.addAttribute("shippingCost", shippingCost);
    model.addAttribute("tax", tax);
    model.addAttribute("totalOrderPrice", total);

    model.addAttribute("orderPriceFormatted", formatter.format(subtotal));
    model.addAttribute("shippingCostFormatted", formatter.format(shippingCost));
    model.addAttribute("taxFormatted", formatter.format(tax));
    model.addAttribute("totalOrderPriceFormatted", formatter.format(total));
}


    return "/user/order";
}

@Transactional
@PostMapping("/orders")
public String realizarPedido(@RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("email") String email,
                             @RequestParam("mobile") String mobile,
                             @RequestParam("address") String address,
                             @RequestParam("city") String city,
                             @RequestParam("state") String state,
                             @RequestParam("shippingMethod") String shippingMethod,
                             @RequestParam("paymentMethod") String paymentMethod,
                             Principal principal,
                             HttpSession session,
                             Model model) {

    if (principal == null) return "redirect:/signin";

    if (address == null || address.trim().length() < 10) {
        session.setAttribute("errorMsg", "Por favor, ingresa una dirección de entrega válida.");
        return "redirect:/user/orders";
    }

    User user = getLoggedUserDetails(principal);
    List<Cart> carts = cartService.getCartsByUser(user.getId());

    if (carts.isEmpty()) {
        session.setAttribute("errorMsg", "El carrito está vacío.");
        return "redirect:/user/cart";
    }

double shippingCost = switch (shippingMethod) {
    case "express" -> 20000;
    case "pickup" -> 0;
    default -> 10000;
};
double tax = 10000;


    double subtotal = carts.stream().mapToDouble(Cart::getTotalPrice).sum();
    tax = 10000; // sin el tipo 'double'
    double total = subtotal + tax + shippingCost;

    List<ProductOrder> orders = new ArrayList<>();
    OrderAddress orderAddress = new OrderAddress();
    boolean addressSaved = false;

for (Cart cart : carts) {
    Product product = cart.getProduct();

    if (product.getProductStock() < cart.getQuantity()) {
        session.setAttribute("errorMsg", "No hay suficiente stock para: " + product.getProductTitle());
        return "redirect:/user/cart";
    }

    ProductOrder order = new ProductOrder();
    order.setUser(user);
    order.setProduct(product);
    order.setQuantity(cart.getQuantity());

    // ✅ Cálculo proporcional de tax y envío por ítem
    order.setPrice(cart.getTotalPrice()); // Solo el precio del producto (sin duplicar impuestos/envío)

    order.setOrderDate(new Date());
    order.setStatus("PENDING");
    order.setOrderId(UUID.randomUUID().toString());
    order.setPaymentType(paymentMethod);
    order.setShippingType(shippingMethod);

    productOrderRepository.save(order);
    orders.add(order);

    // Guardar dirección solo una vez
    if (!addressSaved) {
        orderAddress.setOrder(order);
        orderAddress.setFirstName(firstName);
        orderAddress.setLastName(lastName);
        orderAddress.setEmail(email);
        orderAddress.setMobile(mobile);
        orderAddress.setAddress(address);
        orderAddress.setCity(city);
        orderAddress.setState(state);
        orderAddressRepository.save(orderAddress);
        addressSaved = true;
    }

    // Descontar stock del producto
    product.setProductStock(product.getProductStock() - cart.getQuantity());
    productRepository.save(product);
}

// Vaciar carrito después de completar pedido
cartRepository.deleteAll(carts);


    // Envío de factura por correo
    try {
        byte[] pdfBytes = InvoicePDFGenerator.generateInvoicePDF(user, orderAddress, orders, subtotal, shippingCost, tax, total);

        String htmlBody = """
            <html>
            <body style='font-family:Arial,sans-serif;color:#333;'>
                <h2 style='color:#b22222;'>¡Gracias por tu compra en DECORANDO!</h2>
                <p>Hola <strong>%s</strong>,</p>
                <p>Adjunto encontrarás tu factura por el pedido realizado el día <strong>%s</strong>.</p>
                <p>¡Esperamos que disfrutes tu compra!</p>
                <br/>
                <p style='font-size:0.9em;color:gray;'>Este es un mensaje automático. No respondas este correo.</p>
            </body>
            </html>
        """.formatted(user.getFullName(), new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

        emailService.sendInvoiceEmailWithAttachment(
            user.getEmail(),
            "Factura de tu pedido en DECORANDO",
            htmlBody,
            pdfBytes,
            "factura-decorando.pdf"
        );

    } catch (Exception e) {
        e.printStackTrace();
        session.setAttribute("errorMsg", "Tu pedido fue recibido, pero ocurrió un error al enviar la factura.");
    }

    session.setAttribute("successMsg", "¡Pedido realizado con éxito! Total pagado: $" + (int) total);
    return "redirect:/user/my-orders";
}



    @GetMapping("/order-summary")
    public String verResumenPedidos(Principal principal, Model model) {
        User user = getLoggedUserDetails(principal);
        List<ProductOrder> orders = productOrderRepository.findByUser(user);
        model.addAttribute("orders", orders);
        return "/user/order-summary";
    }

@GetMapping("/my-orders")
public String viewMyOrders(Principal principal, Model model) {
    User user = getLoggedUserDetails(principal);
    List<ProductOrder> orders = productOrderRepository.findByUser(user);

    double totalOrdenado = orders.stream()
        .mapToDouble(order -> order.getPrice() != null ? order.getPrice() : 0)
        .sum();

    int impuestos = 10000;
    int envio = 10000; // Estático por ahora. O puedes detectar el último `shippingType` usado
    double totalConCargos = totalOrdenado + impuestos + envio;

    model.addAttribute("orders", orders);
    model.addAttribute("totalOrdenado", totalOrdenado);
    model.addAttribute("impuestos", impuestos);
    model.addAttribute("envio", envio);
    model.addAttribute("totalConCargos", totalConCargos);

    return "/user/my-orders";
}



} 
