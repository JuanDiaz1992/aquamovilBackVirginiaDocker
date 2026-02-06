package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica;

import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RutasCriticasDesktopServices {
    private final ISolicitudLecturaCritica iSolicitudLecturaCritica;
    private final IRutaLecturaCritica iRutaLecturaCritica;
    private final IUserRepository iUserRepository;
    private final ICliente iCliente;

    public ResponseEntity<Map<String,Object>> getAllLecturasCriticasDesktop() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<RutaLecturaCritica> rutas = iSolicitudLecturaCritica.findDistinctRutasConSolicitudes();
            List<User> users = iUserRepository.findAll();

            response.put("routes", rutas);
            response.put("users", users);
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al realizar la consulta");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }


    public ResponseEntity<Map<String, Object>> asignarRutasCriticasServices(Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try{
            if(!data.isEmpty()){
                List<Map<String, Object>> asignaciones = (List<Map<String, Object>>) data.get("asignaciones");
                for (Map<String, Object> pair : asignaciones) {
                    Long idRuta = Long.valueOf(pair.get("ruta").toString());
                    Long idUser = Long.valueOf(pair.get("user").toString());
                    RutaLecturaCritica ruta = iRutaLecturaCritica.findById(idRuta).orElse(null);
                    User user = iUserRepository.findById(idUser).orElse(null);
                    ruta.setUser(user);
                    iRutaLecturaCritica.save(ruta);
                }
                response.put("status",200);
                response.put("message", "Asignación realizada");
            }else {
                response.put("status",400);
                response.put("message", "No se envió ninguna asignación");
            }
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            response.put("status",400);
            response.put("message", "Error al realizar la asignación");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
