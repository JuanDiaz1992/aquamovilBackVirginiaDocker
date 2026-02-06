package com.springboot.aldiabackjava.controller.aquamovil;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.ProductosYServiciosServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.TipoSolicitudServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.autorizacionesTrabajo.AutorizacionServices;
import com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.presolicitudes.SolicitudServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/autorizaciones")
@RequiredArgsConstructor
public class AutorizacionesTrabajoController {

    private final AutorizacionServices autorizacionServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/get-autorizaciones")
    public ResponseEntity<Map<String, Object>> getAutorizacionesController() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("Solicitud GET de autorizaciones realizada por usuario: {}", user.getUsername());
        return autorizacionServices.getAutorizacionesrabajo();
    }

    @PostMapping(value = "/sincronizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sincronizarAutorizacion(
            @RequestPart("data") String data,
            @RequestPart(value = "multimedia", required = false) MultipartFile[] multimedia
    ) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("Solicitud POST de sincronización realizada por usuario: {} - Datos: {}", user.getUsername(), data);
        return autorizacionServices.setAutorizacionesServices(data, multimedia);
    }

}
