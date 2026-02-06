package com.springboot.aldiabackjava.services.AquaMovilServices;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.repositories.INotificaciones;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServices {
    private final JwtInterceptor jwtInterceptor;
    private final INotificaciones iNotificaciones;

    public ResponseEntity<Map<Object,String>> getNotificaciones(){
        Map<Object,String> response = new HashMap<>();
        return ResponseEntity.ok().body(response);
    }
}
