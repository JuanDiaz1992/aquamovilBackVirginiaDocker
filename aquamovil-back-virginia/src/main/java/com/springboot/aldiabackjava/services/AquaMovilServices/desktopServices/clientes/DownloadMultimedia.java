package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
@RequiredArgsConstructor
@Service
public class DownloadMultimedia {
    @Value("${user.photos.base.path}")
    private String USER_PHOTOS_BASE_PATH;

    public ResponseEntity<InputStreamResource> downloadZippedPhotos() throws IOException {
        Path photosDir = Paths.get(USER_PHOTOS_BASE_PATH);
        if (!Files.exists(photosDir)) {
            return ResponseEntity.status(404)
                    .body(null);
        }

        // Crear archivo temporal
        Path tempZip = Files.createTempFile("multimedia", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(tempZip))) {
            Files.walk(photosDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(photosDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }

        InputStreamResource resource = new InputStreamResource(Files.newInputStream(tempZip));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=multimedia.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(tempZip))
                .body(resource);
    }

}
