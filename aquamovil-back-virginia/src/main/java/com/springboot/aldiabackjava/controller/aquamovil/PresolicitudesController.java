package com.springboot.aldiabackjava.controller.aquamovil;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.ProductosYServiciosServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.TipoSolicitudServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.presolicitudes.SolicitudServices;
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
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
public class PresolicitudesController {
    private final SolicitudServices solicitudServices;
    private final ProductosYServiciosServices productosYServiciosServices;
    private final TipoSolicitudServices tipoSolicitudServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("solicitudes")
    public ResponseEntity<Map<String, Object>> getSolicitudController() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/solicitudes/solicitudes solicitado por {}", user.getUsername());
        return solicitudServices.getSolicitudTrabajo();
    }

    @GetMapping("/getproductosyservicios")
    public ResponseEntity<Map<String, Object>> getProductosYServiciosController() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/solicitudes/getproductosyservicios solicitado por {}", user.getUsername());
        return productosYServiciosServices.getProductosYServiciosService();
    }

    @GetMapping("tiposolicitudes")
    public ResponseEntity<Map<String, Object>> getTipoSolicitudController() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/solicitudes/tiposolicitudes solicitado por {}", user.getUsername());
        return tipoSolicitudServices.getTiposSolicitudesServices();
    }

    @PostMapping(value = "/sincronizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sincronizarPresolicitudes(
            @RequestPart("data") String data,
            @RequestPart(value = "multimedia", required = false) MultipartFile[] multimedia
    ) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/solicitudes/sincronizar solicitado por {}. Data: {}, Archivos: {}",
                user.getUsername(), data, multimedia != null ? multimedia.length : 0);
        return solicitudServices.setSolicitudesServices(data, multimedia);
    }
}