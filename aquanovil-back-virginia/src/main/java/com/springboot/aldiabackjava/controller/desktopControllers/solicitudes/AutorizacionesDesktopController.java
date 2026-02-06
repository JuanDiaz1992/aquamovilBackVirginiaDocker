package com.springboot.aldiabackjava.controller.desktopControllers.solicitudes;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.autorizacionesTrabajo.AutorizacionesDektopServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/desktop/autorizaciones")
@RequiredArgsConstructor
@Slf4j
public class AutorizacionesDesktopController {

    private final AutorizacionesDektopServices autorizacionesDektopServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/get-autorizaciones")
    public ResponseEntity<Map<String, Object>> getAutorizacionesDektopController() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/autorizaciones/get-autorizaciones solicitado por {}", user.getUsername());
        return autorizacionesDektopServices.getAutorizacionesTrabajoServices();
    }

    @PostMapping("/asigna-autorizacion")
    public ResponseEntity<Map<String, Object>> asignarAutorizacionDektopController(@RequestBody Map<String, Object> data) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/autorizaciones/asigna-autorizacion solicitado por {} con datos: {}", user.getUsername(), data);
        return autorizacionesDektopServices.asignarAutorizacionTrabajo(data);
    }

    @DeleteMapping("/delete-autorizacion/{idAutorizacion}")
    public ResponseEntity<Map<String, Object>> deleteAutorizacionDeDesktopController(@PathVariable Long idAutorizacion) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /api/v1/desktop/autorizaciones/delete-autorizacion/{} solicitado por {}", idAutorizacion, user.getUsername());
        return autorizacionesDektopServices.deleteAutorizacion(idAutorizacion);
    }
}
