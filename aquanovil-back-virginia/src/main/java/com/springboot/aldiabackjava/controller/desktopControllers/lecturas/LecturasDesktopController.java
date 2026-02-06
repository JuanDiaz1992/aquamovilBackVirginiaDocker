package com.springboot.aldiabackjava.controller.desktopControllers.lecturas;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.lecturas.RutasDesktopServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/desktop/lecturas")
@RequiredArgsConstructor
@Slf4j
public class LecturasDesktopController {

    private final RutasDesktopServices rutasServicesDesktop;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/getroutes")
    public ResponseEntity<Map<String, Object>> getAllRoutes() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/lecturas/getroutes solicitado por {}", user.getUsername());
        return rutasServicesDesktop.getAllRoutes();
    }

    @PostMapping("/asignar/rutas")
    public ResponseEntity<Map<String, Object>> asignarRutas(@RequestBody Map<String, Object> data) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/lecturas/asignar/rutas solicitado por {} con datos: {}", user.getUsername(), data);
        return rutasServicesDesktop.asignarRutasServices(data);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Map<String, Object>> eliminarLecturas() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /api/v1/desktop/lecturas/delete-all solicitado por {} ", user.getUsername());
        return rutasServicesDesktop.eliminarLecturas();
    }
}
