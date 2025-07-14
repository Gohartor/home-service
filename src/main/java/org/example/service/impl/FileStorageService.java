package org.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

//    private final String uploadDir = "/uploads/profile-photos/";

    public static String saveProfilePhoto(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get("/uploads/profile-photos/", filename);


        Files.createDirectories(filepath.getParent());


        Files.copy(
                file.getInputStream(),
                filepath,
                StandardCopyOption.REPLACE_EXISTING
        );


        return "/profile-photos/" + filename;
    }
}
