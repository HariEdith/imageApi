package com.javatechie.service;

import com.javatechie.entity.ImageData;
import com.javatechie.respository.StorageRepository;
import com.javatechie.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository repository;

    public String uploadImage(MultipartFile file, String productName, double rate, String description) throws IOException {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        // Compress the image data
        byte[] compressedImageData = ImageUtils.compressImage(file.getBytes());

        // Create a new ImageData object
        ImageData imageData = ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(compressedImageData)
                .productName(productName)
                .rate(rate)
                .description(description)
                .build();

        // Save the image data to the database
        imageData = repository.save(imageData);

        // Check if the image data was successfully saved
        if (imageData != null) {
            return "File uploaded successfully: " + file.getOriginalFilename();
        } else {
            throw new RuntimeException("Failed to upload file");
        }
    }

    public byte[] downloadImage(String fileName) {
        // Find the ImageData by name
        Optional<ImageData> optionalImageData = repository.findByName(fileName);

        // Check if ImageData exists
        if (optionalImageData.isPresent()) {
            ImageData imageData = optionalImageData.get();
            // Decompress and return the image data
            return ImageUtils.decompressImage(imageData.getImageData());
        } else {
            throw new IllegalArgumentException("Image not found with name: " + fileName);
        }
    }
}
