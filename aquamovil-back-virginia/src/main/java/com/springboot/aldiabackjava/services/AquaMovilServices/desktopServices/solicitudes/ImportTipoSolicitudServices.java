package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes;

import com.springboot.aldiabackjava.models.solicitudes.TipoSolicitud;
import com.springboot.aldiabackjava.repositories.solicitudes.ITipoSolicitud;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportTipoSolicitudServices {
    private final Conversores conversores;
    private final ITipoSolicitud iTipoSolicitud;
    public ResponseEntity<Map<String,Object>> setTipoSolicitud(MultipartFile file){
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, TipoSolicitud> tipoSolicitudMap = iTipoSolicitud.findAll().stream()
                    .collect(Collectors.toMap(TipoSolicitud::getIdTipoSolicitud, c -> c));
            List<TipoSolicitud> tipoSolicitudList = new ArrayList<>();
            Set<Long> yaInsertados = new HashSet<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (fields.length < 4 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {

                        Long idTipoSolicitud = conversores.parseLongSafe(fields[0]);
                        String tipoSolicitud = fields[1];
                        Integer direccionamiento = conversores.parseIntSafe(fields[2]);
                        Integer cargoFuncional = fields[3].isBlank()? null: conversores.parseIntSafe(fields[3]);
                        Boolean referencia = conversores.convertirABoolean(fields[4]);
                        Boolean comercial = conversores.convertirABoolean(fields[5]);

                        if (!tipoSolicitudMap.containsKey(idTipoSolicitud) && !yaInsertados.contains(idTipoSolicitud)) {
                            TipoSolicitud nuevoTipoSolicitud = TipoSolicitud.builder()
                                    .idTipoSolicitud(idTipoSolicitud)
                                    .tipoSolicitud(tipoSolicitud)
                                    .direccionamiento(direccionamiento)
                                    .cargoFuncional(cargoFuncional)
                                    .referencia(referencia)
                                    .comercial(comercial)
                                    .build();
                            tipoSolicitudList.add(nuevoTipoSolicitud);
                            yaInsertados.add(idTipoSolicitud);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iTipoSolicitud.saveAll(tipoSolicitudList);

                response.put("message", "Importación completada. Total registros: " + tipoSolicitudList.size());
                response.put("status", 200);
                return ResponseEntity.ok(response);

            } catch (IOException e) {
                log.error("Error leyendo archivo", e);
                response.put("message", "Error al leer archivo: " + e.getMessage());
                response.put("status", 500);
                return ResponseEntity.internalServerError().body(response);
            }

        } catch (Exception e) {
            log.error("Error en importación", e);
            response.put("message", "Error en importación: " + e.getMessage());
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
