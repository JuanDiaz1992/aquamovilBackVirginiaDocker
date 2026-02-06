package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.lecturas;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturas.ILecturas;
import com.springboot.aldiabackjava.repositories.lecturas.IMultimediaLecturas;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RutasDesktopServices {
    private final IRuta iRuta;
    private final IUserRepository iUserRepository;
    private final ICliente iCliente;
    private final ILecturas iLecturas;
    private final IMultimediaLecturas iMultimediaLecturas;

    public ResponseEntity<Map<String, Object>> getAllRoutes() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = iUserRepository.findAll();
            List<Ruta> rutasConClientes = iRuta.findRutasConClientes();

            response.put("routes", rutasConClientes);
            response.put("users", users);
            response.put("status", 200);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            String mensaje = "Error al consultar las rutas, " + e;
            response.put("message", mensaje);
            response.put("status", 500);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> asignarRutasServices(Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try{
            if(!data.isEmpty()){
                List<Map<String, Object>> asignaciones = (List<Map<String, Object>>) data.get("asignaciones");

                for (Map<String, Object> pair : asignaciones) {
                    Long idRuta = Long.valueOf(pair.get("ruta").toString());
                    Long idUser = Long.valueOf(pair.get("user").toString());
                    Ruta ruta = iRuta.findById(idRuta).orElse(null);
                    User user = iUserRepository.findById(idUser).orElse(null);
                    ruta.setUser(user);
                    iRuta.save(ruta);
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

    @Transactional
    public ResponseEntity<Map<String, Object>> eliminarLecturas() {
        Map<String, Object> response = new HashMap<>();
        try{
            List<Cliente> clienteList = iCliente.findAll().stream()
                    .peek(cliente -> {
                        cliente.setLecturaCompletada(false);
                    })
                    .collect(Collectors.toList());
            iCliente.saveAll(clienteList);
            iMultimediaLecturas.deleteAllMultimediaLecturas();
            iLecturas.deleteAllLecturas();
            List<Ruta> rutaList = iRuta.findAll().stream()
                    .peek(ruta->{
                        ruta.setEstado(false);
                        ruta.setUser(null);
                    }).collect(Collectors.toList());
            iRuta.saveAll(rutaList);
            response.put("status",200);
            response.put("message", "Eliminación completada");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("status",400);
            response.put("message", "Error al realizar la eliminación");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
