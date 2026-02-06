package com.springboot.aldiabackjava.controller.desktopControllers.solicitudes;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.ProductosYServiciosDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.ImportTipoSolicitudServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.preSolicitudes.ImportSolicitudesDeTrabajoServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.preSolicitudes.SolicitudesDesktopServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/desktop/docs")
@RequiredArgsConstructor
@Slf4j
public class SolicitudesInsertDesktopController {
    private final ProductosYServiciosDesktopServices productosYServiciosDesktopServices;
    private final ImportSolicitudesDeTrabajoServices importSolicitudesDeTrabajoServices;
    private final ImportTipoSolicitudServices importTipoSolicitudServices;
    private final SolicitudesDesktopServices solicitudesDesktopServices;
    private final JwtInterceptor jwtInterceptor;

    @PostMapping(value = "/insert/productos-servicios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarClientes(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/docs/insert/productos-servicios solicitado por {} - Archivo: {}", user.getUsername(), file.getOriginalFilename());
        return productosYServiciosDesktopServices.importProductosYServiciosService(file);
    }

    @PostMapping(value = "/insert/solicitudes-trabajo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importSolicitudesTrabajo(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/docs/insert/solicitudes-trabajo solicitado por {} - Archivo: {}", user.getUsername(), file.getOriginalFilename());
        return importSolicitudesDeTrabajoServices.importSolicitudesTrabajo(file);
    }

    @PostMapping(value = "/insert/tipo-solicitudes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importTiposSolicitudes(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/desktop/docs/insert/tipo-solicitudes solicitado por {} - Archivo: {}", user.getUsername(), file.getOriginalFilename());
        return importTipoSolicitudServices.setTipoSolicitud(file);
    }

    @GetMapping("/export/solicitudes")
    public ResponseEntity<byte[]> getSolicitudesCsv() {
        return solicitudesDesktopServices.exportSolicitudesYSubCotizacionesCSV();
    }
}