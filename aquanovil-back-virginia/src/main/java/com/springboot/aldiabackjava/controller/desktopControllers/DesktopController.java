package com.springboot.aldiabackjava.controller.desktopControllers;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.UtilsServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes.ClientesDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.lecturas.BasicTablesLecturasServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.ProductosYServiciosDesktopServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.TipoSolicitudServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/desktop")
@RequiredArgsConstructor
@Slf4j
public class DesktopController {

    private final ClientesDesktopServices clientesDesktopServices;
    private final JwtInterceptor jwtInterceptor;
    private final ProductosYServiciosDesktopServices productosYServiciosDesktopServices;
    private final TipoSolicitudServices tipoSolicitudServices;
    private final UtilsServices utilsServices;
    private final BasicTablesLecturasServices basicTablesLecturasServices;

    @GetMapping("/getallusers")
    public ResponseEntity<Map<String, Object>> obtenerTodosLosClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/getallusers solicitado por {} - page: {}, size: {}", user.getUsername(), page, size);
        return clientesDesktopServices.getAllClientes(page, size);
    }

    @GetMapping("/get-all-basical-tables")
    public ResponseEntity<Map<String, Object>> obtenerTablasPrincipales(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/get-all-basical-tables solicitado por {}", user.getUsername());
        return utilsServices.getAllBasicaltables();
    }

    @DeleteMapping("/delete-all-productos-y-servicios")
    public ResponseEntity<Map<String, Object>> deleteAllProductosYServicios(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/delete-all-productos-y-servicios solicitado por {} ", user.getUsername());
        return productosYServiciosDesktopServices.deleteAllProductosYServiciosService();
    }

    @DeleteMapping("/delete-all-tipo-solicitudes")
    public ResponseEntity<Map<String, Object>> deleteAllTiposSolicitudes(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/delete-all-tipo-solicitudes solicitado por {} ", user.getUsername());
        return tipoSolicitudServices.deleteAllTipoSolicitudes();
    }

    @DeleteMapping("/delete-all-parametros")
    public ResponseEntity<Map<String, Object>> deleteAllParametros(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/delete-all-parametros solicitado por {} ", user.getUsername());
        return basicTablesLecturasServices.deleteAllParametros();
    }

    @DeleteMapping("/delete-all-causales")
    public ResponseEntity<Map<String, Object>> deleteAllCausales(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/delete-all-causales solicitado por {} ", user.getUsername());
        return basicTablesLecturasServices.deleteAllCausales();
    }


    @DeleteMapping("/delete-all-observaciones")
    public ResponseEntity<Map<String, Object>> deleteAllObservaciones(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/delete-all-observaciones solicitado por {} ", user.getUsername());
        return basicTablesLecturasServices.deleteAllObservaciones();
    }

    @DeleteMapping("/delete-all-rutas")
    public ResponseEntity<Map<String, Object>> deleteAllORutas(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/delete-all-rutas solicitado por {} ", user.getUsername());
        return basicTablesLecturasServices.deleteAllRutas();
    }


}