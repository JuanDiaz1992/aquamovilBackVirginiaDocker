package com.springboot.aldiabackjava.controller.desktopControllers;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.UtilsServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/desktop/utils")
@RequiredArgsConstructor
@Slf4j
public class UtilsController {
    private final UtilsServices utilsServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("/get-log/{date}")
    public ResponseEntity<Resource> getLog(@PathVariable String date) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/desktop/get-log de la fecha {}, solicitado por {} ", date, user.getUsername());
        return utilsServices.exportLog(date);
    }
    @DeleteMapping("/limpiarbd")
    public ResponseEntity<Map<String, Object>> limpiarBaseDeDatos() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /limpiarbd solicitado por {}", user.getUsername());
        return utilsServices.deleteAllTables();
    }
}
