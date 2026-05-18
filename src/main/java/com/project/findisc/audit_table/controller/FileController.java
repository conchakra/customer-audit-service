package com.project.findisc.audit_table.controller;

import com.project.findisc.audit_table.storage.FileStorageProvider;

import java.nio.file.Files;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    private final FileStorageProvider storageProvider;

    public FileController(FileStorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

  @GetMapping("/{fileName:.+}")
public ResponseEntity<Resource> getFile(@PathVariable String fileName) throws Exception {

    Resource resource = storageProvider.getFile(fileName);

    String contentType = Files.probeContentType(resource.getFile().toPath());

    return ResponseEntity.ok()
            .header("Content-Type", contentType)
            .body(resource);
}
}