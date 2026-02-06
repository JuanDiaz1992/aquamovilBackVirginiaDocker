package com.springboot.aldiabackjava.services.AquaMovilServices.criticas;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.HistorialLecturas;
import com.springboot.aldiabackjava.models.lecturasCriticas.CriticaLectura;
import com.springboot.aldiabackjava.models.lecturasCriticas.SolicitudLecturaCritica;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.IHistorialLecturas;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ICriticaLectura;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RutasLecturaCriticaServices {
    private final JwtInterceptor jwtInterceptor;
    private final ICliente iCliente;
    private final IRutaLecturaCritica iRutaLecturaCritica;
    private final ICriticaLectura iCriticaLectura;
    private final ISolicitudLecturaCritica iSolicitudLecturaCritica;
    private final IHistorialLecturas iHistorialLecturas;

    public ResponseEntity<Map<String, Object>> getRoutes() {
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Obtener rutas asignadas
            List<RutaLecturaCritica> rutasLecturasCriticas = iRutaLecturaCritica.findByUserId(user.getIdUser());

            Map<Long, List<Cliente>> clientesPorRuta = new HashMap<>();
            Map<Long, Integer> recuentoClientes = new HashMap<>();
            Map<Long, List<Map<String, Object>>> criticasPorCliente = new HashMap<>();
            List<Map<String, Object>> historialLecturas = new ArrayList<>();
            List<Map<String, Object>> cleanSolicitudes = new ArrayList<>();

            // 2. Obtener clientes por todas las rutas del usuario
            List<Long> rutaIds = rutasLecturasCriticas.stream()
                    .map(RutaLecturaCritica::getIdRutaLecturaCritica)
                    .collect(Collectors.toList());

            List<Cliente> todosClientes = iCliente.findByRutaIds(rutaIds); // Esta función debes tenerla en el repo
            Map<Long, List<Cliente>> agrupadosPorRuta = todosClientes.stream()
                    .filter(c -> c.getRuta() != null)
                    .collect(Collectors.groupingBy(
                            c -> c.getRuta().getIdRuta(),
                            Collectors.toList()
                    ));

            // 3. Obtener todas las solicitudes de una sola vez
            List<SolicitudLecturaCritica> todasSolicitudes = iSolicitudLecturaCritica.findAll();
            Map<Long, List<SolicitudLecturaCritica>> solicitudesPorCliente = todasSolicitudes.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getCliente().getIdCliente()
                    ));

            // 4. Filtrar clientes con solicitudes
            Map<Long, List<Cliente>> clientesFiltradosPorRuta = new HashMap<>();
            for (RutaLecturaCritica ruta : rutasLecturasCriticas) {
                List<Cliente> clientesRuta = agrupadosPorRuta.getOrDefault(ruta.getIdRutaLecturaCritica(), new ArrayList<>());
                List<Cliente> filtrados = clientesRuta.stream()
                        .filter(c -> solicitudesPorCliente.containsKey(c.getIdCliente()))
                        .collect(Collectors.toList());

                clientesPorRuta.put(ruta.getIdRutaLecturaCritica(), filtrados);
                recuentoClientes.put(ruta.getIdRutaLecturaCritica(), filtrados.size());
                clientesFiltradosPorRuta.put(ruta.getIdRutaLecturaCritica(), filtrados);
            }

              // 5. Historial de lecturas en batch
            List<Long> clienteIds = todosClientes.stream()
                    .map(Cliente::getIdCliente)
                    .collect(Collectors.toList());

            List<HistorialLecturas> todasLecturas = iHistorialLecturas.findByCliente_IdClienteIn(clienteIds);

            Map<Long, List<Map<String, Object>>> historialPorCliente = todasLecturas.stream()
                    .collect(Collectors.groupingBy(
                            l -> l.getCliente().getIdCliente(),
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    lista -> lista.stream()
                                            .limit(6) 
                                            .map(l -> {
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("idLectura", l.getIdLectura());
                                                map.put("idCliente", l.getCliente().getIdCliente());
                                                map.put("lectura", l.getLectura());
                                                return map;
                                            })
                                            .collect(Collectors.toList())
                            )
                    ));
            // 6. Lecturas críticas (en batch)
            List<CriticaLectura> criticas = iCriticaLectura.findByClienteIds(clienteIds);

            Map<Long, CriticaLectura> criticaPorCliente = criticas.stream()
                    .collect(Collectors.toMap(
                            c -> c.getCliente().getIdCliente(),
                            c -> c
                    ));

            for (Map.Entry<Long, List<Cliente>> entry : clientesFiltradosPorRuta.entrySet()) {
                Long rutaId = entry.getKey();
                List<Cliente> clientes = entry.getValue();

                List<Map<String, Object>> criticasLecturas = clientes.stream()
                        .filter(c -> Boolean.TRUE.equals(c.getCriticaLecturaCompletada()))
                        .map(c -> {
                            CriticaLectura cl = criticaPorCliente.get(c.getIdCliente());
                            if (cl == null) return null;
                            Map<String, Object> map = new HashMap<>();
                            map.put("idCriticaLectura", cl.getIdCriticaLectura());
                            map.put("fecha", cl.getFecha());
                            map.put("idCliente", c.getIdCliente());
                            map.put("lectura", cl.getLectura());
                            map.put("idUser", cl.getUser() != null ? cl.getUser().getIdUser() : null);
                            map.put("atendio", cl.getAtendio());
                            map.put("documento", cl.getDocumentoAntendio());
                            map.put("telefono", cl.getTelefono());
                            map.put("observaciones", cl.getObservaciones());
                            map.put("respuestas", cl.getRespuestas());
                            map.put("idSolicitudCriticaLectura", cl.getSolicitudLecturaCritica().getIdSolicitudCriticaLectura());
                            map.put("idRuta",c.getRutaLecturaCritica().getIdRutaLecturaCritica());
                            return map;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                log.info(criticasLecturas.toString());
                criticasPorCliente.put(rutaId, criticasLecturas);
            }

            // 7. Formatear las solicitudes
            cleanSolicitudes = todasSolicitudes.stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("idSolicitudLecturaCritica", s.getIdSolicitudCriticaLectura());
                        map.put("idCliente", s.getCliente().getIdCliente());
                        map.put("motivo", s.getMotivo());
                        map.put("completada", s.isCompletada());
                        map.put("consumo", s.getConsumo());
                        map.put("lectura", s.getLectura());
                        map.put("causal", s.getCausal() != null ? s.getCausal().getIdCausal() : null);
                        map.put("acueducto", s.getAcueducto());
                        map.put("alcantarillado", s.getAlcantarillado());
                        map.put("aseo", s.getAseo());
                        map.put("promedio", s.getPromedio());
                        map.put("tieneMedidor", s.getTieneMedidor());

                        // Observaciones actuales
                        map.put("observacion1", s.getObservacion1() != null ? s.getObservacion1().getIdObservacion() : null);
                        map.put("observacion2", s.getObservacion2() != null ? s.getObservacion2().getIdObservacion() : null);
                        map.put("observacion3", s.getObservacion3() != null ? s.getObservacion3().getIdObservacion() : null);

                        // Anteriores
                        map.put("causalAnt", s.getCausalAnt() != null ? s.getCausalAnt().getIdCausal() : null);
                        map.put("observacion1Ant", s.getObservacion1Ant() != null ? s.getObservacion1Ant().getIdObservacion() : null);
                        map.put("observacion2Ant", s.getObservacion2Ant() != null ? s.getObservacion2Ant().getIdObservacion() : null);
                        map.put("observacion3Ant", s.getObservacion3Ant() != null ? s.getObservacion3Ant().getIdObservacion() : null);

                        return map;
                    })
                    .collect(Collectors.toList());

            // 8. Construcción final
            response.put("rutaLecturaCritica", rutasLecturasCriticas);
            response.put("clientes", clientesPorRuta);
            response.put("recuentoClientes", recuentoClientes);
            response.put("criticasPorCliente", criticasPorCliente);
            response.put("historialLecturas", historialPorCliente);
            response.put("solicitudLecturaCriticas", cleanSolicitudes);
            response.put("status", 200);

        } catch (Exception e) {
            log.error("Error en getRoutes: {}", e.getMessage(), e);
            response.put("status", 500);
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok().body(response);
    }

}
