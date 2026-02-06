package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices;


import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IObservaciones;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IParametros;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturas.ILecturas;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExportAndImportServices {
    private final ICausal iCausal;
    private final IObservaciones iObservaciones;
    private final IParametros iParametros;
    private final Conversores conversores;



    @Transactional
    public ResponseEntity<Map<String, Object>> importarCausales(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, Causal> causalMap = iCausal.findAll().stream()
                    .collect(Collectors.toMap(Causal::getIdCausal, c -> c));
            List<Causal> causalList = new ArrayList<>();
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
                        Long idCausal = conversores.parseLongSafe(fields[0]);
                        String causal = fields[1];
                        Boolean requiereObservacion = Boolean.valueOf(fields[2]);
                        Boolean requiereFoto = Boolean.valueOf(fields[3]);

                        if (!causalMap.containsKey(idCausal) && !yaInsertados.contains(idCausal)) {
                            Causal nuevoCausal = new Causal();
                            nuevoCausal.setIdCausal(idCausal);
                            nuevoCausal.setNombre(causal);
                            nuevoCausal.setRequiereFoto(requiereFoto);
                            nuevoCausal.setRequiereObservacion(requiereObservacion);
                            causalList.add(nuevoCausal);
                            yaInsertados.add(idCausal);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iCausal.saveAll(causalList);

                response.put("message", "Importación completada. Total registros: " + causalList.size());
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

    @Transactional
    public ResponseEntity<Map<String, Object>> importObservaciones(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, Observaciones> observacionesMap = iObservaciones.findAll().stream()
                    .collect(Collectors.toMap(Observaciones::getIdObservacion, c -> c));
            List<Observaciones> observacionList = new ArrayList<>();
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
                    if (fields.length < 3 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {
                        Long idObservacion = conversores.parseLongSafe(fields[0]);
                        String observacion = fields[1];
                        Boolean requiereFoto = Boolean.valueOf(fields[2]);

                        if (!observacionesMap.containsKey(idObservacion) && !yaInsertados.contains(idObservacion)) {
                            Observaciones newObservacion = new Observaciones();
                            newObservacion.setIdObservacion(idObservacion);
                            newObservacion.setDescripcion(observacion);
                            newObservacion.setRequiereFoto(requiereFoto);
                            observacionList.add(newObservacion);
                            yaInsertados.add(idObservacion);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iObservaciones.saveAll(observacionList);

                response.put("message", "Importación completada. Total registros: " + observacionList.size());
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

    @Transactional
    public ResponseEntity<Map<String, Object>> importParametros(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, Parametros> parametrosMap = iParametros.findAll().stream()
                    .collect(Collectors.toMap(Parametros::getIdParametros, c -> c));
            List<Parametros> parametrosList = new ArrayList<>();
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
                    if (fields.length < 3 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {
                        Long idParametro = conversores.parseLongSafe(fields[0]);
                        String valor = fields[1];
                        String nombre = fields[2];

                        if (!parametrosMap.containsKey(idParametro) && !yaInsertados.contains(idParametro)) {
                            Parametros newParametro = new Parametros();
                            newParametro.setIdParametros(idParametro);
                            newParametro.setValor(valor);
                            newParametro.setNombre(nombre);
                            parametrosList.add(newParametro);
                            yaInsertados.add(idParametro);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }
                log.info(parametrosList.toString());
                iParametros.saveAll(parametrosList);

                response.put("message", "Importación completada. Total registros: " + parametrosList.size());
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
