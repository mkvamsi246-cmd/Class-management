package com.smartclassroom.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smartclassroom.service.ExcelUploadService;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private ExcelUploadService excelUploadService;

    @PostMapping("/timetable")
    public String uploadTimetable(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        return excelUploadService.uploadTimetable(file);
    }
}