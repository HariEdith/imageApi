package com.javatechie.controller;

import com.javatechie.entity.ImageData;
import com.javatechie.respository.StorageRepository;
import com.javatechie.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private StorageService service;

    @Autowired
    private StorageRepository repository;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestParam("productName") String productName,
                                         @RequestParam("rate") double rate,
                                         @RequestParam("description") String description) throws IOException {
        String uploadImage = service.uploadImage(file, productName, rate, description);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImageByName(@PathVariable String fileName) {
        byte[] imageData = service.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllImageData() {
        List<ImageData> allImageData = repository.findAll();
        if (!allImageData.isEmpty()) {
            List<Map<String, Object>> responseData = new ArrayList<>();
            for (ImageData imageData : allImageData) {
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", imageData.getId());
                imageInfo.put("name", imageData.getName());
                imageInfo.put("type", imageData.getType());
                imageInfo.put("productName", imageData.getProductName());
                imageInfo.put("rate", imageData.getRate());
                imageInfo.put("description", imageData.getDescription());
                // You can add more fields as needed
                responseData.add(imageInfo);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseData);
        } else {
            // Handle the case where there are no image data in the database
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No image data found");
        }
    }
}
