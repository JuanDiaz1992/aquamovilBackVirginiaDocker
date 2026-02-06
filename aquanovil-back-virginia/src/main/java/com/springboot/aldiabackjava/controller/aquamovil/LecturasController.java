package com.springboot.aldiabackjava.controller.aquamovil;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.AditionalServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.lecturas.CrcServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.lecturas.RutasServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.springboot.aldiabackjava.services.AquaMovilServices.lecturas.LecturasServices;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/lecturas")
@RequiredArgsConstructor
public class LecturasController {
    private final LecturasServices lecturasServices;
    private final RutasServices rutasServices;
    private final CrcServices crcServices;
    private final AditionalServices aditionalServices;
    private final JwtInterceptor jwtInterceptor;

    @PostMapping(value = "/sincronizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sincronizarLecturas(
            @RequestPart("data") String data,
            @RequestPart(value = "photos", required = false) MultipartFile[] photos,
            @RequestPart(value = "rutas", required = false) String rutasCompletadas
    ) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/lecturas/sincronizar solicitado por {}. Data: {}, RutasCompletadas: {}, Archivos: {}",
                user.getUsername(), data, rutasCompletadas, photos != null ? photos.length : 0);
        return lecturasServices.setLecturas(data, photos, rutasCompletadas);
    }

    @PostMapping("/crc")
    public ResponseEntity<Map<String,Object>> sincronizarCrc(@RequestBody Map<String, Object> crc){
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/lecturas/crc solicitado por {}. Body: {}", user.getUsername(), crc);
        return crcServices.setCrc(crc);
    }

    @GetMapping("/getroutes")
    public ResponseEntity<Map<String,Object>> getRoutes(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/lecturas/getroutes solicitado por {}", user.getUsername());
        return rutasServices.getRoutes();
    }

    @GetMapping("/otertables")
    public ResponseEntity<Map<String, Object>> getOterTables(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/lecturas/otertables solicitado por {}", user.getUsername());
        return aditionalServices.getOterTables();
    }
}


