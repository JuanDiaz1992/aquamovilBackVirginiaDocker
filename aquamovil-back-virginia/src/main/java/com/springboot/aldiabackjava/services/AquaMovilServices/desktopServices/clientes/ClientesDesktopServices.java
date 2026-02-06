package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.repositories.ICiclo;
import com.springboot.aldiabackjava.repositories.IHistorialLecturas;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturas.ILecturas;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ICriticaLectura;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientesDesktopServices {
    private final ICliente iCliente;
    private final ILecturas iLecturas;
    private final Conversores conversores;


    public ResponseEntity<Map<String, Object>> getAllClientes(int page, int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Cliente> clientes = this.getClientesPaginados(page, size);

            List<Long> idsClientes = clientes.getContent().stream()
                    .map(Cliente::getIdCliente)
                    .collect(Collectors.toList());

            List<Lectura> lecturas = iLecturas.findByCliente_IdClienteIn(idsClientes);
            Map<Long, Lectura> lecturasPorCliente = lecturas.stream()
                    .collect(Collectors.toMap(l -> l.getCliente().getIdCliente(), l -> l));

            Page<Map<String, Object>> clientesLimpios = clientes.map(cliente -> {
                Lectura lectura = lecturasPorCliente.get(cliente.getIdCliente());
                Map<String, Object> cl = new HashMap<>();
                cl.put("idCliente", cliente.getIdCliente());
                cl.put("nombre", cliente.getNombre());
                cl.put("idRuta", cliente.getRuta().getIdRuta());
                cl.put("ciclo", cliente.getRuta().getCiclo().getIdCiclo());
                cl.put("consecutivo", cliente.getConsecutivo());
                cl.put("direccion", cliente.getDireccion());
                cl.put("nMedidor", cliente.getNMedidor());
                cl.put("catMedidor", cliente.getCatMedidor());
                cl.put("causal", cliente.getCausal().getIdCausal());
                cl.put("obsAnterior", cliente.getObsAnterior());
                cl.put("ultimaLectura", cliente.getUltimaLectura());
                cl.put("promedio", cliente.getPromedio());
                cl.put("idUso", cliente.getIdUso());
                cl.put("categoria", cliente.getCategoria());

                if (lectura != null) {
                    Map<String, Object> lecturaMap = new HashMap<>();
                    lecturaMap.put("lectura", lectura.getLectura());
                    lecturaMap.put("causal", lectura.getCausal());
                    lecturaMap.put("observacion1", lectura.getObservacion1());
                    lecturaMap.put("observacion2", lectura.getObservacion2());
                    lecturaMap.put("observacion3", lectura.getObservacion3());
                    lecturaMap.put("fecha", lectura.getFecha());

                    if (lectura.getUser() != null) {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("idUser", lectura.getUser().getIdUser());
                        lecturaMap.put("user", userMap);
                    }

                    cl.put("lectura", lecturaMap);
                } else {
                    cl.put("lectura", null);
                }

                return cl;
            });
            log.info(clientesLimpios.toString());

            response.put("clientes", clientesLimpios);
            response.put("status", 200);
            response.put("totalClientes", clientes.getTotalElements());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al realizar la consulta");
            response.put("status", 500);
            return ResponseEntity.badRequest().body(response);
        }
    }

    public Page<Cliente> getClientesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iCliente.findAll(pageable);
    }

    public ResponseEntity<byte[]> exportarClientesCSV() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024); // Buffer inicial de 1MB

            // BOM para UTF-8
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);

            try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                // Escribir encabezados
                writer.write("IdUsuario,IdCiclo,IdRuta,Ruta,DireccionPredio,NumeroMedidor,TipoMedidor,LecturaAnterior," +
                        "CausalAnterior,Observacion1Anterior,promedio,NombreUsuario,IdUso,IdCategoria,Lectura,Causal," +
                        "Observacion1,Observacion2,Observacion3,Usuario,FhGrabacion\n");

                // Procesar todos los clientes (versión simplificada sin paginación)
                List<Cliente> clientes = iCliente.findAll();

                // Precargar todas las lecturas en un mapa
                Map<Long, Lectura> lecturasMap = iLecturas != null ?
                        iLecturas.findAll().stream()
                                .collect(Collectors.toMap(l -> l.getCliente().getIdCliente(), Function.identity())) :
                        Collections.emptyMap();

                // Procesar cada cliente
                for (Cliente cliente : clientes) {
                    Lectura lectura = lecturasMap.get(cliente.getIdCliente());

                    // Construir línea CSV
                    writer.write(
                            conversores.safe(cliente.getIdCliente()) + "," +
                                    conversores.safe(cliente.getRuta() != null && cliente.getRuta().getCiclo() != null ?
                                            cliente.getRuta().getCiclo().getCiclo() : null) + "," +
                                    conversores.safe(cliente.getRuta() != null ? cliente.getRuta().getIdRuta() : null) + "," +
                                    conversores.safe(cliente.getConsecutivo()) + "," +
                                    conversores.safe(cliente.getDireccion()) + "," +
                                    conversores.safe(cliente.getNMedidor()) + "," +
                                    conversores.safe(cliente.getCatMedidor()) + "," +
                                    conversores.safe(cliente.getUltimaLectura()) + "," +
                                    conversores.safe(cliente.getCausal() != null ? cliente.getCausal().getIdCausal() : null) + "," +
                                    conversores.safe(cliente.getObsAnterior()) + "," +
                                    conversores.safe(cliente.getPromedio()) + "," +
                                    conversores.safe(cliente.getNombre()) + "," +
                                    conversores.safe(cliente.getIdUso()) + "," +
                                    conversores.safe(cliente.getCategoria()) + "," +
                                    (lectura != null ?
                                            conversores.safe(lectura.getLectura()) + "," +
                                                    conversores.safe(lectura.getCausal() != null ? lectura.getCausal().getIdCausal() : null) + "," +
                                                    conversores.safe(lectura.getObservacion1() != null ? lectura.getObservacion1().getIdObservacion() : null) + "," +
                                                    conversores.safe(lectura.getObservacion2() != null ? lectura.getObservacion2().getIdObservacion() : null) + "," +
                                                    conversores.safe(lectura.getObservacion3() != null ? lectura.getObservacion3().getIdObservacion() : null) + "," +
                                                    conversores.safe(lectura.getUser() != null ? lectura.getUser().getIdUser() : null) + "," +
                                                    conversores.safe(lectura.getFecha())
                                            : ",,,,,,,"
                                    ) + "\n"
                    );
                }

                writer.flush();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.csv")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(out.toByteArray());

        } catch (Exception e) {
            log.error("Error en exportación CSV", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
