package com.springboot.aldiabackjava.services.AquaMovilServices.lecturas;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturas.ILecturas;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RutasServices {
    private final JwtInterceptor jwtInterceptor;
    private final ICliente iCliente;
    private final IRuta iRuta;
    private final ILecturas iLectura;

    public ResponseEntity<Map<String, Object>> getRoutes() {
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();

        try {
            Map<Long, List<Cliente>> clientesPorRuta = new HashMap<>();
            Map<Long, Integer> recuentoClientes = new HashMap<>();
            Map<Long, List<Map<String, Object>>> lecturasPorCliente = new HashMap<>();

            List<Ruta> rutas = iRuta.findByUserId(user.getIdUser());

            rutas.forEach(ruta -> {
                List<Cliente> clientes = iCliente.findByRuta_IdRuta(ruta.getIdRuta());


                clientesPorRuta.put(ruta.getIdRuta(), clientes);
                recuentoClientes.put(ruta.getIdRuta(), clientes.size());

                List<Map<String, Object>> cleanLecturas = clientes.stream()
                        .filter(cliente -> cliente.getLecturaCompletada() != null && cliente.getLecturaCompletada())
                        .map(cliente -> {
                            Optional<Lectura> lecturaOpt = Optional.ofNullable(iLectura.findByCliente(cliente));

                            if (lecturaOpt.isPresent()) {
                                Lectura lectura = lecturaOpt.get();
                                Map<String, Object> cleanLectura = new HashMap<>();
                                cleanLectura.put("idCliente", cliente.getIdCliente());
                                cleanLectura.put("idCausal", lectura.getCausal() != null ? lectura.getCausal().getIdCausal() : null);
                                cleanLectura.put("lectura", lectura.getLectura());
                                cleanLectura.put("idObservacion1", lectura.getObservacion1() != null ? lectura.getObservacion1().getIdObservacion() : null);
                                cleanLectura.put("idObservacion2", lectura.getObservacion2() != null ? lectura.getObservacion2().getIdObservacion() : null);
                                cleanLectura.put("idObservacion3", lectura.getObservacion3() != null ? lectura.getObservacion3().getIdObservacion() : null);
                                cleanLectura.put("nivelAlerta", lectura.getParametros());
                                cleanLectura.put("idUser", lectura.getUser() != null ? lectura.getUser().getIdUser() : null);
                                cleanLectura.put("idRuta", cliente.getRuta().getIdRuta());
                                cleanLectura.put("fecha", lectura.getFecha());
                                return cleanLectura;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();

                lecturasPorCliente.put(ruta.getIdRuta(), cleanLecturas);
            });

            response.put("rutas", rutas);
            response.put("clientes", clientesPorRuta);
            response.put("recuentoClientes", recuentoClientes);
            response.put("lecturas", lecturasPorCliente);  // Añadir lecturas a la respuesta
            response.put("status", 200);

        } catch (Exception e) {
            log.error("Error en getRoutes: {}", e.getMessage(), e);
            response.put("status", 500);
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok().body(response);
    }


}
