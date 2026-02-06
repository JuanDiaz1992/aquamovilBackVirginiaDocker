package com.springboot.aldiabackjava.controller.desktopControllers.solicitudes;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.preSolicitudes.SolicitudesDesktopServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/desktop/solicitudes")
@RequiredArgsConstructor
@Slf4j
public class SolicitudesDesktopController {

    private final SolicitudesDesktopServices solicitudesDesktopServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/get-solicitudes")
    public ResponseEntity<Map<String, Object>> getSolicitudes() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/solicitudes/get-solicitudes solicitado por {}", user.getUsername());
        return solicitudesDesktopServices.getSolicitudesDesktopServices();
    }

    @PostMapping("/asigna-solicitud")
    public ResponseEntity<Map<String, Object>> asignarAutorizacionDektopController(@RequestBody Map<String, Object> data) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/solicitudes/asigna-solicitud solicitado por {} - Datos: {}", user.getUsername(), data);
        return solicitudesDesktopServices.asignarSolicitudTrabajo(data);
    }
}

