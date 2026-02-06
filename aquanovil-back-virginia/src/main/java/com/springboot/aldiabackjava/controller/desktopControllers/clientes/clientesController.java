package com.springboot.aldiabackjava.controller.desktopControllers.clientes;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes.CrcDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes.DownloadMultimedia;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;




@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/desktop/clientes")
public class clientesController {
    private final DownloadMultimedia downloadMultimedia;
    private final CrcDesktopServices crcDesktopServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/getmedia")
    public ResponseEntity<InputStreamResource> getMedia() throws IOException {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/clientes/getmedia solicitado por {}", user.getUsername());
        return downloadMultimedia.downloadZippedPhotos();
    }

    @GetMapping("/getcrc")
    public ResponseEntity<Map<String, Object>> getAllCrcs() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/clientes/getcrc solicitado por {}", user.getUsername());
        return crcDesktopServices.getAllCrc();
    }
}
