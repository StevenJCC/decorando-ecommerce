package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.service.BulkUploadService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class FileUploadController {

    @Autowired
    private BulkUploadService bulkUploadService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;

    /**
     * Muestra el panel de administración
     */
    @GetMapping("")
    public String adminDashboard() {
        return "admin/admin-dashboard"; // Asegúrate de que el archivo se llama admin-dashboard.html y está en /templates
    }

    /**
     * Maneja la carga masiva de productos desde un archivo CSV o Excel
     */
    @PostMapping("/upload-products")
    public String uploadProductsFile(@RequestParam("archivo") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("productMessage", "El archivo está vacío");
                redirectAttributes.addFlashAttribute("productSuccess", false);
                return "redirect:/admin";
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || !(fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                redirectAttributes.addFlashAttribute("productMessage", "Formato de archivo no soportado. Use CSV o Excel (.xlsx, .xls)");
                redirectAttributes.addFlashAttribute("productSuccess", false);
                return "redirect:/admin";
            }
            
            List<Product> productos = bulkUploadService.processProductFile(file);
            
            int count = 0;
            for (Product producto : productos) {
                productService.saveProduct(producto);
                count++;
            }
            
            redirectAttributes.addFlashAttribute("productMessage", "Archivo procesado correctamente. " + count + " productos importados.");
            redirectAttributes.addFlashAttribute("productSuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("productMessage", "Error al procesar el archivo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("productSuccess", false);
        }
        return "redirect:/admin";
    }

    /**
     * Maneja la carga masiva de categorías desde un archivo CSV o Excel
     */
    @PostMapping("/upload-categories")
    public String uploadCategoriesFile(@RequestParam("archivo") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("categoryMessage", "El archivo está vacío");
                redirectAttributes.addFlashAttribute("categorySuccess", false);
                return "redirect:/admin";
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || !(fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                redirectAttributes.addFlashAttribute("categoryMessage", "Formato de archivo no soportado. Use CSV o Excel (.xlsx, .xls)");
                redirectAttributes.addFlashAttribute("categorySuccess", false);
                return "redirect:/admin";
            }
            
            List<Category> categorias = bulkUploadService.processCategoryFile(file);
            
            int count = 0;
            for (Category categoria : categorias) {
                categoryService.saveCategory(categoria);
                count++;
            }
            
            redirectAttributes.addFlashAttribute("categoryMessage", "Archivo procesado correctamente. " + count + " categorías importadas.");
            redirectAttributes.addFlashAttribute("categorySuccess", true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("categoryMessage", "Error al procesar el archivo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("categorySuccess", false);
        }
        return "redirect:/admin";
    }

    /**
     * Proporciona una plantilla de ejemplo para productos
     */
    @GetMapping("/download-product-template")
    public void downloadProductTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=plantilla_productos.xlsx");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"product_title", "product_description", "product_price", "discount", "product_stock", "product_category"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Cortina Modelo Flores");
            dataRow.createCell(1).setCellValue("Cortina elegante con diseño floral, material 100% poliéster");
            dataRow.createCell(2).setCellValue(49.99);
            dataRow.createCell(3).setCellValue(10);
            dataRow.createCell(4).setCellValue(25);
            dataRow.createCell(5).setCellValue("CORTINAS MODERNAS");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * Proporciona una plantilla de ejemplo para categorías
     */
    @GetMapping("/download-category-template")
    public void downloadCategoryTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=plantilla_categorias.xlsx");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Categorías");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"category_name", "category_image"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("CORTINAS MODERNAS");
            dataRow.createCell(1).setCellValue("cortinas.jpg");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}
