package com.springboot.aldiabackjava.controller.desktopControllers.solicitudes;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.autorizacionesTrabajo.AutorizacionesDektopServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/desktop/docs")
@RequiredArgsConstructor
@Slf4j
public class AutorizacionesInsertDesktopController {

    private final AutorizacionesDektopServices autorizacionesDektopServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/export/autorizaciones")
    public ResponseEntity<byte[]> getSolicitudesCsv() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/docs/export/autorizaciones solicitado por {}", user.getUsername());
        return autorizacionesDektopServices.exportAutorizacionesYSubCotizacionesCSV();
    }
}
