package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.lecturas;

import com.springboot.aldiabackjava.models.Ciclo;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.repositories.ICiclo;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportLecturasServices {
    private final IRuta iRuta;
    private final IRutaLecturaCritica iRutaLecturaCritica;
    private final ICiclo iCiclo;
    private final Conversores conversores;

    @Transactional
    public ResponseEntity<Map<String, Object>> importarRutasCSV(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, Ciclo> cicloMap = iCiclo.findAll().stream()
                    .collect(Collectors.toMap(Ciclo::getIdCiclo, c -> c));

            List<Ruta> rutasNormales = new ArrayList<>();
            List<RutaLecturaCritica> rutasCriticas = new ArrayList<>();
            int totalProcessed = 0;

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


                    if (fields.length == 1) {
                        log.error("La línea no contiene separadores válidos (coma): '{}'", line);
                        response.put("message", "Formato inválido. Asegúrate de que el archivo esté separado por comas (CSV válido).");
                        response.put("status", 400);
                        return ResponseEntity.badRequest().body(response);
                    }

                    if (fields.length < 2 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {
                        Long idRuta = conversores.parseLongSafe(fields[1]);
                        Integer nombreRuta = conversores.parseIntSafe(fields[1]);
                        Long idCiclo = conversores.parseLongSafe(fields[0]);
                        // Crear ciclo si no existe
                        if (!cicloMap.containsKey(idCiclo)) {
                            Ciclo nuevoCiclo = new Ciclo();
                            nuevoCiclo.setIdCiclo(idCiclo);
                            nuevoCiclo.setCiclo(Math.toIntExact(idCiclo));
                            nuevoCiclo = iCiclo.save(nuevoCiclo);
                            cicloMap.put(idCiclo, nuevoCiclo);
                            log.info("Ciclo creado: {}", nuevoCiclo.getIdCiclo());
                        }

                        Ciclo ciclo = cicloMap.get(idCiclo);

                        // Crear ruta normal
                        Ruta ruta = new Ruta();
                        ruta.setIdRuta(idRuta);
                        ruta.setRuta(nombreRuta);
                        ruta.setCiclo(ciclo);
                        rutasNormales.add(ruta);

                        // Crear ruta crítica (sin usuario)
                        RutaLecturaCritica critica = new RutaLecturaCritica();
                        critica.setIdRutaLecturaCritica(idRuta);
                        critica.setRuta(idRuta.intValue());
                        critica.setCiclo(ciclo);
                        rutasCriticas.add(critica);

                        totalProcessed++;

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                // Guardar todas las rutas normales y críticas
                iRuta.saveAll(rutasNormales);
                iRutaLecturaCritica.saveAll(rutasCriticas);

                log.info("Rutas normales guardadas: {}", rutasNormales.size());
                log.info("Rutas críticas guardadas: {}", rutasCriticas.size());

                response.put("message", "Importación completada. Total registros: " + totalProcessed);
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
