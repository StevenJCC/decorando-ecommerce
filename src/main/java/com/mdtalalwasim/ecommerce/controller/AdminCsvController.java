package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.CsvService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCsvController {

    @Autowired
    private CsvService csvService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductService productService;

    /**
     * Muestra la vista para cargar archivos CSV
     */
    @GetMapping("/categorias/carga")
    public String showCsvUploadForm(Model model) {
        // Pasa las categorías al modelo para el formulario de productos
        // Usando el método correcto de tu servicio
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/csv_upload";
    }

    /**
     * Procesa la carga de productos desde CSV
     */
    @PostMapping("/csv/upload/products")
    public String uploadProductsCsv(@RequestParam("file") MultipartFile file, 
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Por favor selecciona un archivo");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/categorias/carga";
        }

        try {
            // Procesar el archivo CSV
            List<Product> products = csvService.parseProductsCsv(file);
            
            // Guardar los productos en la base de datos
            int savedCount = 0;
            for (Product product : products) {
                try {
                    // Usando el método correcto de tu servicio
                    productService.saveProduct(product);
                    savedCount++;
                } catch (Exception e) {
                    // Log error but continue processing
                    System.err.println("Error al guardar producto: " + e.getMessage());
                }
            }
            
            redirectAttributes.addFlashAttribute("message", 
                "Archivo subido correctamente. " + savedCount + " productos importados.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                "Error al procesar el archivo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/admin/categorias/carga";
    }

    /**
     * Procesa la carga de categorías desde CSV
     */
    @PostMapping("/csv/upload/categories")
    public String uploadCategoriesCsv(@RequestParam("file") MultipartFile file, 
                                     RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Por favor selecciona un archivo");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/categorias/carga";
        }

        try {
            // Procesar el archivo CSV
            List<Category> categories = csvService.parseCategoriesCsv(file);
            
            // Guardar las categorías en la base de datos
            int savedCount = 0;
            for (Category category : categories) {
                try {
                    // Usando el método correcto de tu servicio
                    categoryService.saveCategory(category);
                    savedCount++;
                } catch (Exception e) {
                    // Log error but continue processing
                    System.err.println("Error al guardar categoría: " + e.getMessage());
                }
            }
            
            redirectAttributes.addFlashAttribute("message", 
                "Archivo subido correctamente. " + savedCount + " categorías importadas.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                "Error al procesar el archivo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/admin/categorias/carga";
    }
}