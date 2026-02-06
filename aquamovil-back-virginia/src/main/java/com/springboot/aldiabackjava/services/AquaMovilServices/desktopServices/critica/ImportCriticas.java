package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.HistorialLecturas;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.lecturasCriticas.SolicitudLecturaCritica;
import com.springboot.aldiabackjava.repositories.IHistorialLecturas;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IObservaciones;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IParametros;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportCriticas {
    private final ICliente iCliente;
    private final ICausal iCausal;
    private final IObservaciones iObservaciones;
    private final IParametros iParametros;
    private final ISolicitudLecturaCritica iSolicitudLecturaCritica;
    private final IHistorialLecturas iHistorialLecturas;
    private final Conversores conversores;

    @Transactional
    public ResponseEntity<Map<String, Object>> importCriticas(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }
        List<Cliente> clientes = iCliente.findAll();
        if (clientes.isEmpty()){
            response.put("message", "Importa primero la base completa de clientes");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            log.info("Precargando archivo");
            List<SolicitudLecturaCritica> totalSolicitudesAGuardar = new ArrayList<>();
            List<HistorialLecturas> historialLecturasList = new ArrayList<>();
            List<Observaciones> observaciones = iObservaciones.findAll();
            List<Causal> causalList = iCausal.findAll();
            List<Parametros> parametros = iParametros.findAll();
            int totalProcessed = 0;
            int skippedRecords = 0;

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
                    if (fields.length < 41 || isBlankOrNull(fields[0]) || isBlankOrNull(fields[1])) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        skippedRecords++;
                        continue;
                    }

                    try {
                        Long clienteId = conversores.parseLongSafe(fields[0]);
                        if (clienteId == null) {
                            log.warn("ID de cliente inválido en línea: {}", line);
                            skippedRecords++;
                            continue;
                        }

                        Cliente cliente = clientes.stream()
                                .filter(c -> clienteId.equals(c.getIdCliente()))
                                .findFirst()
                                .orElse(null);

                        if (cliente == null) {
                            log.warn("Cliente no encontrado con ID: {}", clienteId);
                            skippedRecords++;
                            continue;
                        }

                        List<SolicitudLecturaCritica> listSolicitudLecturaCriticasCliente = iSolicitudLecturaCritica.findByCliente(cliente);
                        boolean yaTieneSolicitudEsteMes = listSolicitudLecturaCriticasCliente.stream().anyMatch(solicitud -> {
                            Calendar fechaSolicitud = Calendar.getInstance();
                            fechaSolicitud.setTime(solicitud.getFechaCreacion());

                            Calendar ahora = Calendar.getInstance();

                            return fechaSolicitud.get(Calendar.YEAR) == ahora.get(Calendar.YEAR)
                                    && fechaSolicitud.get(Calendar.MONTH) == ahora.get(Calendar.MONTH);
                        });

                        if (!yaTieneSolicitudEsteMes){
                            SolicitudLecturaCritica solicitudLecturaCritica = SolicitudLecturaCritica.builder()
                                    .cliente(cliente)
                                    .acueducto(conversores.convertirABoolean(fields[7]))
                                    .alcantarillado(conversores.convertirABoolean(fields[8]))
                                    .aseo(conversores.convertirABoolean(fields[9]))
                                    .motivo(getStringOrNull(fields[10]))
                                    .lectura(conversores.parseIntSafe(fields[16]))
                                    .consumo(conversores.parseIntSafe(fields[17]))
                                    .promedio(conversores.parseIntSafe(fields[18]))
                                    .tieneMedidor(conversores.convertirABoolean(fields[25]))
                                    .causal(getCausalOrNull(causalList, fields[15]))
                                    .observacion1(getObservacionOrNull(observaciones, fields[26]))
                                    .observacion2(getObservacionOrNull(observaciones, fields[27]))
                                    .observacion3(getObservacionOrNull(observaciones, fields[28]))
                                    .observacion1Ant(getObservacionOrNull(observaciones, fields[35]))
                                    .observacion2Ant(getObservacionOrNull(observaciones, fields[36]))
                                    .observacion3Ant(getObservacionOrNull(observaciones, fields[37]))
                                    .causalAnt(getCausalOrNull(causalList, fields[34]))
                                    .build();
                            totalSolicitudesAGuardar.add(solicitudLecturaCritica);

                            // Procesar historial de lecturas
                            for (int i = 0; i <= 5; i++) {
                                Integer lectura = conversores.parseIntSafe(fields[19 + i]);
                                if (lectura != null) {
                                    HistorialLecturas historialLecturas = HistorialLecturas.builder()
                                            .cliente(cliente)
                                            .lectura(lectura)
                                            .build();
                                    historialLecturasList.add(historialLecturas);
                                }
                            }
                        }
                        totalProcessed++;

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                        skippedRecords++;
                    }
                }

                log.info("Total Solicitudes a guardar: {}", totalSolicitudesAGuardar.size());
                log.info("Registros omitidos: {}", skippedRecords);

                // Guardar todas las solicitudes y historiales
                iSolicitudLecturaCritica.saveAll(totalSolicitudesAGuardar);
                iHistorialLecturas.saveAll(historialLecturasList);

                response.put("message", "Importación completada. Total procesados: " + totalProcessed +
                        ", Registros guardados: " + totalSolicitudesAGuardar.size() +
                        ", Registros omitidos: " + skippedRecords);
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

    // Métodos auxiliares para manejar valores nulos
    private boolean isBlankOrNull(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null");
    }

    private String getStringOrNull(String value) {
        return isBlankOrNull(value) ? null : value.trim();
    }

    private Observaciones getObservacionOrNull(List<Observaciones> observaciones, String fieldValue) {
        if (isBlankOrNull(fieldValue)) return null;
        Long id = conversores.parseLongSafe(fieldValue);
        if (id == null) return null;
        return observaciones.stream()
                .filter(o -> id.equals(o.getIdObservacion()))
                .findFirst()
                .orElse(null);
    }

    private Causal getCausalOrNull(List<Causal> causales, String fieldValue) {
        if (isBlankOrNull(fieldValue)) return null;
        Long id = conversores.parseLongSafe(fieldValue);
        if (id == null) return null;
        return causales.stream()
                .filter(c -> id.equals(c.getIdCausal()))
                .findFirst()
                .orElse(null);
    }

}
