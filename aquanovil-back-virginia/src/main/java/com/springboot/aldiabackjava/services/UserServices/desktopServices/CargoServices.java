package com.springboot.aldiabackjava.services.UserServices.desktopServices;

import com.springboot.aldiabackjava.models.userModels.Cargo;
import com.springboot.aldiabackjava.repositories.userRepositories.ICargo;
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
public class CargoServices {
    private final ICargo iCargo;

    public ResponseEntity<Map<String, Object>> getAllCargos(){
        Map<String, Object> response = new HashMap<>();
        try{
            List<Cargo> cargos = iCargo.findAll();
            response.put("cargos", cargos);
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            response.put("message", "Error al realizar la consulta");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
