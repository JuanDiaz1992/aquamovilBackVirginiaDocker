package com.springboot.aldiabackjava.controller.aquamovil;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.AditionalServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.criticas.CriticaLecturaServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.criticas.RutasLecturaCriticaServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/critica-lecturas")
@RequiredArgsConstructor
public class CriticasLecturasController {

    private final RutasLecturaCriticaServices rutasLecturaCriticaServices;
    private final AditionalServices aditionalServices;
    private final CriticaLecturaServices criticaLecturaServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/getroutes")
    public ResponseEntity<Map<String,Object>> getRoutes() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("Usuario '{}' solicitó las rutas de lectura crítica", user.getUsername());
        return rutasLecturaCriticaServices.getRoutes();
    }

    @GetMapping("/getpreguntas")
    public ResponseEntity<Map<String, Object>> getPreguntas() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("Usuario '{}' solicitó las preguntas de lectura crítica", user.getUsername());
        return aditionalServices.getPreguntasCritica();
    }

    @PostMapping(value = "/sincronizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sincronizarLecturas(
            @RequestPart("data") String data,
            @RequestPart(value = "multimedia", required = false) MultipartFile[] multimedia
    ) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("Usuario '{}' solicitó sincronización de lecturas críticas con data: {} y archivos: {}",
                user.getUsername(), data, multimedia != null ? multimedia.length : 0);
        return criticaLecturaServices.setCriticas(data, multimedia);
    }
}
