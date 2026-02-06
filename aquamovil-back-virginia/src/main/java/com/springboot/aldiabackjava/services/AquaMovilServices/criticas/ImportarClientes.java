package com.springboot.aldiabackjava.services.AquaMovilServices.criticas;

import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportarClientes {
    private final IRuta iRuta;
    private final IRutaLecturaCritica iRutaLecturaCritica;
    private final ICausal iCausal;
    private final DataSource dataSource;
    private final Conversores conversores;

    @Transactional
    public ResponseEntity<Map<String, Object>> importarClientesCSV(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        List<Ruta> rutas = iRuta.findAll();
        if (rutas.isEmpty()){
            response.put("message", "Ingresa primero las rutas");
            return ResponseEntity.badRequest().body(response);
        }
        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            // 1. Precargar entidades relacionadas
            Map<Integer, Long> rutasMap = iRuta.findAll().stream()
                    .collect(Collectors.toMap(Ruta::getRuta, Ruta::getIdRuta));

            Map<Integer, Long> rutasCriticasMap = iRutaLecturaCritica.findAll().stream()
                    .collect(Collectors.toMap(RutaLecturaCritica::getRuta, RutaLecturaCritica::getIdRutaLecturaCritica));

            Map<Long, Long> causalesMap = iCausal.findAll().stream()
                    .collect(Collectors.toMap(Causal::getIdCausal, Causal::getIdCausal));

            // 2. Configurar JdbcTemplate
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.setFetchSize(1000);

            // 3. SQL de inserción
            String insertCliente = "INSERT INTO clientes (" +
                    "id_cliente, nombre, fk_ruta_asociada, fk_ruta_lectura_critica, " +
                    "consecutivo, direccion, n_medidor, cat_medidor, fk_causal_anterior, " +
                    "obs_anterior, ultima_lectura, id_uso, categoria, promedio) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // 4. Procesamiento del archivo
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                boolean firstLine = true;
                int batchSize = 1000;
                List<Object[]> batchParams = new ArrayList<>(batchSize);
                int totalProcessed = 0;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (fields.length < 21) continue;

                    try {
                        Object[] params = new Object[]{
                                conversores.parseLongSafe(fields[0]),                                 // id_cliente
                                conversores.cleanField(fields[11]),                                   // nombre
                                rutasMap.get(conversores.parseIntSafe(fields[2])),                    // fk_ruta_asociada
                                rutasCriticasMap.get(conversores.parseIntSafe(fields[2])),            // fk_ruta_lectura_critica
                                conversores.parseIntSafe(fields[3]),                                  // consecutivo
                                conversores.cleanField(fields[4]),                                    // direccion
                                conversores.cleanField(fields[5]),                                    // n_medidor
                                conversores.parseIntSafe(fields[6]),                                  // cat_medidor
                                causalesMap.get(conversores.parseLongSafe(fields[8])),                // fk_causal_anterior
                                conversores.parseIntSafe(fields[9]),                                  // obs_anterior
                                conversores.parseIntSafe(fields[7]),                                  // ultima_lectura
                                conversores.parseIntSafe(fields[12]),                                 // id_uso
                                conversores.parseIntSafe(fields[13]),                                 // categoria
                                conversores.parseIntSafe(fields[10]),                                 // id_x
                        };

                        batchParams.add(params);

                        if (batchParams.size() >= batchSize) {
                            jdbcTemplate.batchUpdate(insertCliente, batchParams);
                            totalProcessed += batchParams.size();
                            batchParams.clear();
                            log.info("Registros procesados: {}", totalProcessed);
                        }

                    } catch (DuplicateKeyException dk) {
                        String msg = "Hay datos ya existentes, valida el contenido e intentalo de nuevo";
                        log.error(msg);
                        response.put("message", msg);
                        response.put("status", 400);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                        throw e;
                    }
                }

                // Procesar batch final
                if (!batchParams.isEmpty()) {
                    jdbcTemplate.batchUpdate(insertCliente, batchParams);
                    totalProcessed += batchParams.size();
                }
                log.info("Carga finalizada");
                response.put("message", "Importación completada. Total registros: " + totalProcessed);
                response.put("status", 200);
                return ResponseEntity.ok(response);

            } catch (IOException e) {
                log.error("Error leyendo archivo", e);
                response.put("message", "Error al leer archivo: " + e.getMessage());
                response.put("status", 400);
                return ResponseEntity.internalServerError().body(response);
            }

        } catch (Exception e) {
            log.error("Error en importación", e);
            response.put("message", e.getMessage());
            response.put("status", 400);
            return ResponseEntity.internalServerError().body(response);
        }
    }



}
