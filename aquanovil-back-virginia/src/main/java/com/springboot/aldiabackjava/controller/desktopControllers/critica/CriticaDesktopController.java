package com.springboot.aldiabackjava.controller.desktopControllers.critica;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica.CriticasDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica.RutasCriticasDesktopServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/desktop/critica")
@RequiredArgsConstructor
@Slf4j
public class CriticaDesktopController {
    private final RutasCriticasDesktopServices rutasCriticasDesktopServices;
    private final CriticasDesktopServices criticasDesktopServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/getroutescritica")
    public ResponseEntity<Map<String, Object>> getAllRoutesCritica() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/critica/getroutescritica solicitado por {}", user.getUsername());
        return rutasCriticasDesktopServices.getAllLecturasCriticasDesktop();
    }

    @PostMapping("/asignar/rutascriticas")
    public ResponseEntity<Map<String, Object>> asignarRutasCriticas(@RequestBody Map<String, Object> data) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/critica/asignar/rutascriticas solicitado por {} con datos: {}", user.getUsername(), data);
        return rutasCriticasDesktopServices.asignarRutasCriticasServices(data);
    }

    @PostMapping("/solicitudc")
    public ResponseEntity<Map<String, Object>> setSolictudCritica(@RequestBody Map<String, Object> data) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/critica/solicitudc solicitado por {} con datos: {}", user.getUsername(), data);
        return criticasDesktopServices.setSolicitudCritica(data);
    }

    @GetMapping("/getsolicitudesc")
    public ResponseEntity<Map<String, Object>> getSolicitudesCritica() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/critica/getsolicitudesc solicitado por {}", user.getUsername());
        return criticasDesktopServices.getSolicitudesCritica();
    }

    @DeleteMapping("/deletesolcitudc/{idSolicitud}")
    public ResponseEntity<Map<String, Object>> deleteSolicitud(@PathVariable Long idSolicitud) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /api/v1/desktop/critica/deletesolcitudc/{} solicitado por {}", idSolicitud, user.getUsername());
        return criticasDesktopServices.deleteSolicitud(idSolicitud);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Map<String, Object>> eliminarLecturas() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /api/v1/desktop/critica/delete-all solicitado por {} ", user.getUsername());
        return criticasDesktopServices.eliminarCriticaLecturas();
    }
}
