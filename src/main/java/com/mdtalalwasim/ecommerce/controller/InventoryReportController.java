package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.service.InventoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class InventoryReportController {

    @Autowired
    private InventoryReportService reportService;

    @GetMapping("/inventory-report-pdf")
    public ResponseEntity<ByteArrayResource> downloadInventoryReport() throws Exception {
        byte[] pdfData = reportService.generateInventoryReportWithChart();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=inventory_report.pdf")
                .body(new ByteArrayResource(pdfData));
    }
}
