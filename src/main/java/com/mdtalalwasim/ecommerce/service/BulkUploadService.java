package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class BulkUploadService {

    @Autowired
    private CategoryService categoryService;

    /**
     * Procesa un archivo CSV o Excel con productos
     */
    public List<Product> processProductFile(MultipartFile file) throws IOException, CsvValidationException {
        List<Product> products = new ArrayList<>();
        
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("Nombre de archivo inválido");
        }
        
        if (fileName.endsWith(".csv")) {
            products = processProductCSV(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            products = processProductExcel(file);
        } else {
            throw new IOException("Formato de archivo no soportado");
        }
        
        return products;
    }
    
    /**
     * Procesa un archivo CSV o Excel con categorías
     */
    public List<Category> processCategoryFile(MultipartFile file) throws IOException, CsvValidationException {
        List<Category> categories = new ArrayList<>();
        
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("Nombre de archivo inválido");
        }
        
        if (fileName.endsWith(".csv")) {
            categories = processCategoryCSV(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            categories = processCategoryExcel(file);
        } else {
            throw new IOException("Formato de archivo no soportado");
        }
        
        return categories;
    }

    /**
     * Procesa un archivo CSV de productos
     */
    private List<Product> processProductCSV(MultipartFile file) throws IOException, CsvValidationException {
        List<Product> products = new ArrayList<>();
        
        try (
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            CSVReader csvReader = new CSVReader(fileReader);
        ) {
            // Leer encabezados
            String[] headers = csvReader.readNext();
            if (headers == null) {
                throw new IOException("El archivo CSV está vacío o no tiene encabezados");
            }
            
            // Validar encabezados
            validateProductHeaders(headers);
            
            // Leer datos
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                Product product = createProductFromCSVLine(line);
                products.add(product);
            }
        }
        
        return products;
    }
    
    /**
     * Procesa un archivo Excel de productos
     */
    private List<Product> processProductExcel(MultipartFile file) throws IOException {
        List<Product> products = new ArrayList<>();
        
        try (
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Leer encabezados
            if (!rows.hasNext()) {
                throw new IOException("El archivo Excel está vacío");
            }
            
            Row headerRow = rows.next();
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers[i] = cell != null ? cell.getStringCellValue() : "";
            }
            
            // Validar encabezados
            validateProductHeaders(headers);
            
            // Leer datos
            while (rows.hasNext()) {
                Row row = rows.next();
                Product product = createProductFromExcelRow(row);
                products.add(product);
            }
        }
        
        return products;
    }
    
    /**
     * Procesa un archivo CSV de categorías
     */
    private List<Category> processCategoryCSV(MultipartFile file) throws IOException, CsvValidationException {
        List<Category> categories = new ArrayList<>();
        
        try (
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            CSVReader csvReader = new CSVReader(fileReader);
        ) {
            // Leer encabezados
            String[] headers = csvReader.readNext();
            if (headers == null) {
                throw new IOException("El archivo CSV está vacío o no tiene encabezados");
            }
            
            // Validar encabezados
            validateCategoryHeaders(headers);
            
            // Leer datos
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                Category category = createCategoryFromCSVLine(line);
                categories.add(category);
            }
        }
        
        return categories;
    }
    
    /**
     * Procesa un archivo Excel de categorías
     */
    private List<Category> processCategoryExcel(MultipartFile file) throws IOException {
        List<Category> categories = new ArrayList<>();
        
        try (
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Leer encabezados
            if (!rows.hasNext()) {
                throw new IOException("El archivo Excel está vacío");
            }
            
            Row headerRow = rows.next();
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers[i] = cell != null ? cell.getStringCellValue() : "";
            }
            
            // Validar encabezados
            validateCategoryHeaders(headers);
            
            // Leer datos
            while (rows.hasNext()) {
                Row row = rows.next();
                Category category = createCategoryFromExcelRow(row);
                categories.add(category);
            }
        }
        
        return categories;
    }
    
    /**
     * Valida que los encabezados del archivo de productos sean correctos
     */
    private void validateProductHeaders(String[] headers) throws IOException {
        // Encabezados mínimos esperados para productos
        String[] expectedHeaders = {
            "product_title", "product_description", "product_price", 
            "discount", "product_stock", "product_category"
        };
        
        for (String expectedHeader : expectedHeaders) {
            boolean found = false;
            for (String header : headers) {
                if (expectedHeader.equalsIgnoreCase(header.trim())) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                throw new IOException("Falta el encabezado obligatorio: " + expectedHeader);
            }
        }
    }
    
    /**
     * Valida que los encabezados del archivo de categorías sean correctos
     */
    private void validateCategoryHeaders(String[] headers) throws IOException {
        // Encabezados mínimos esperados para categorías
        String[] expectedHeaders = {"category_name"};
        
        for (String expectedHeader : expectedHeaders) {
            boolean found = false;
            for (String header : headers) {
                if (expectedHeader.equalsIgnoreCase(header.trim())) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                throw new IOException("Falta el encabezado obligatorio: " + expectedHeader);
            }
        }
    }
    
    /**
     * Crea un objeto producto a partir de una línea CSV
     */
    private Product createProductFromCSVLine(String[] line) {
        Product product = new Product();
        
        // Mapeo de campos CSV a propiedades del producto
        product.setProductTitle(line[0]);
        product.setProductDescription(line[1]);
        product.setProductPrice(Double.parseDouble(line[2]));
        product.setDiscount(Integer.parseInt(line[3]));
        
        // Calcular precio con descuento
        if (product.getDiscount() > 0) {
            double discountAmount = (product.getProductPrice() * product.getDiscount()) / 100.0;
            product.setDiscountPrice(product.getProductPrice() - discountAmount);
        } else {
            product.setDiscountPrice(product.getProductPrice());
        }
        
        product.setProductStock(Integer.parseInt(line[4]));
        product.setProductCategory(line[5]);
        product.setProductImage("default.jpg"); // Imagen por defecto
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return product;
    }
    
    /**
     * Crea un objeto producto a partir de una fila de Excel
     */
    private Product createProductFromExcelRow(Row row) {
        Product product = new Product();
        
        // Mapeo de celdas Excel a propiedades del producto
        product.setProductTitle(getCellValueAsString(row.getCell(0)));
        product.setProductDescription(getCellValueAsString(row.getCell(1)));
        product.setProductPrice(getCellValueAsDouble(row.getCell(2)));
        product.setDiscount(getCellValueAsInt(row.getCell(3)));
        
        // Calcular precio con descuento
        if (product.getDiscount() > 0) {
            double discountAmount = (product.getProductPrice() * product.getDiscount()) / 100.0;
            product.setDiscountPrice(product.getProductPrice() - discountAmount);
        } else {
            product.setDiscountPrice(product.getProductPrice());
        }
        
        product.setProductStock(getCellValueAsInt(row.getCell(4)));
        product.setProductCategory(getCellValueAsString(row.getCell(5)));
        product.setProductImage("default.jpg"); // Imagen por defecto
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return product;
    }
    
    /**
     * Crea un objeto categoría a partir de una línea CSV
     */
    private Category createCategoryFromCSVLine(String[] line) {
        Category category = new Category();
        
        // Mapeo de campos CSV a propiedades de categoría
        category.setCategoryName(line[0]);
        
        // Si hay más columnas y corresponden a otras propiedades, asignarlas aquí
        if (line.length > 1 && !line[1].isEmpty()) {
            category.setCategoryImage(line[1]);
        } else {
            category.setCategoryImage("default-category.jpg");
        }
        
        category.setIsActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return category;
    }
    
    /**
     * Crea un objeto categoría a partir de una fila de Excel
     */
    private Category createCategoryFromExcelRow(Row row) {
        Category category = new Category();
        
        // Mapeo de celdas Excel a propiedades de categoría
        category.setCategoryName(getCellValueAsString(row.getCell(0)));
        
        // Si hay más columnas y corresponden a otras propiedades, asignarlas aquí
        if (row.getCell(1) != null) {
            String image = getCellValueAsString(row.getCell(1));
            if (!image.isEmpty()) {
                category.setCategoryImage(image);
            } else {
                category.setCategoryImage("default-category.jpg");
            }
        } else {
            category.setCategoryImage("default-category.jpg");
        }
        
        category.setIsActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return category;
    }
    
    /**
     * Obtiene el valor de una celda como cadena
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    
    /**
     * Obtiene el valor de una celda como entero
     */
    private int getCellValueAsInt(Cell cell) {
        if (cell == null) {
            return 0;
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }
    
    /**
     * Obtiene el valor de una celda como double
     */
    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }
}