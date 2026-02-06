package com.springboot.aldiabackjava.controller.desktopControllers;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.criticas.ImportarClientes;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.*;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes.ClientesDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes.CrcDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica.CriticasDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica.ImportCriticas;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.lecturas.ImportLecturasServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.ProductosYServiciosDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.autorizacionesTrabajo.ImportAutorizacionesDeTrabajoServices;
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
public class SetCsv {
    private final ExportAndImportServices exportServices;
    private final ImportLecturasServices importLecturasServices;
    private final ClientesDesktopServices clientesDesktopServices;
    private final ImportCriticas importCriticas;
    private final ImportarClientes importarClientes;
    private final CrcDesktopServices crcDesktopServices;
    private final CriticasDesktopServices criticasDesktopServices;
    private final ProductosYServiciosDesktopServices productosYServiciosDesktopServices;
    private final ImportAutorizacionesDeTrabajoServices importProductosYServiciosService;
    private final JwtInterceptor jwtInterceptor;

    @PostMapping(value = "/insert/clientes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarClientes(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/clientes solicitado por {}", user.getUsername());
        return importarClientes.importarClientesCSV(file);
    }

    @PostMapping(value = "/insert/rutas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarRutas(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/rutas solicitado por {}", user.getUsername());
        return importLecturasServices.importarRutasCSV(file);
    }

    @PostMapping(value = "/insert/causales", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarCausales(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/causales solicitado por {}", user.getUsername());
        return exportServices.importarCausales(file);
    }

    @PostMapping(value = "/insert/observaciones", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarObservaciones(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/observaciones solicitado por {}", user.getUsername());
        return exportServices.importObservaciones(file);
    }

    @PostMapping(value = "/insert/parametros", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarParametros(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/parametros solicitado por {}", user.getUsername());
        return exportServices.importParametros(file);
    }

    @PostMapping(value = "/insert/criticas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importarCriticas(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/criticas solicitado por {}", user.getUsername());
        return importCriticas.importCriticas(file);
    }

    @PostMapping(value = "/insert/productosyservicios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importProductosYServicios(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/productosyservicios solicitado por {}", user.getUsername());
        return productosYServiciosDesktopServices.importProductosYServiciosService(file);
    }

    @PostMapping(value = "/insert/autorizacionestrabajo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importAutorizacionesDeTrabajo(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/autorizacionestrabajo solicitado por {}", user.getUsername());
        return importProductosYServiciosService.getAutorizacionesTrabajoServices(file);
    }

    @PostMapping(value = "/insert/subcotizaciones", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importSubCotizaciones(@RequestParam("file") MultipartFile file) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /insert/subcotizaciones solicitado por {}", user.getUsername());
        return importProductosYServiciosService.getSubCotizaciones(file);
    }

    @GetMapping("/export/clientes")
    public ResponseEntity<byte[]> exportDBClientesCSV() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /export/clientes solicitado por {}", user.getUsername());
        return clientesDesktopServices.exportarClientesCSV();
    }

    @GetMapping("/export/crc")
    public ResponseEntity<byte[]> exportCrc() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /export/crc solicitado por {}", user.getUsername());
        return crcDesktopServices.exportCrcrScv();
    }

    @GetMapping("/export/solicitudesc")
    public ResponseEntity<byte[]> exportSolicitudesCritica() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /export/solicitudesc solicitado por {}", user.getUsername());
        return criticasDesktopServices.exportSolicitudesCSV();
    }
}
