package com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes;

import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.repositories.solicitudes.IProductosYServicios;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductosYServiciosServices {
    private final IProductosYServicios iProductosYServicios;
    public ResponseEntity<Map<String, Object>> getProductosYServiciosService(){
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductosYServicios> productosYServiciosList = iProductosYServicios.findAll();
            response.put("productosyservicios", productosYServiciosList);
            response.put("status",200);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al realizar la consulta, intentelo más tarde");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
