package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.preSolicitudes;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.Solicitud;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes.ISolicitud;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
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
public class ImportSolicitudesDeTrabajoServices {
    private final ICliente iCliente;
    private final IUserRepository iUserRepository;
    private final Conversores conversores;
    private final ISolicitud iSolicitud;
    public ResponseEntity<Map<String,Object>> importSolicitudesTrabajo(MultipartFile file){
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, Solicitud> solicidutesTrabajo = iSolicitud.findAll().stream()
                    .collect(Collectors.toMap(Solicitud::getIdOt, c -> c));
            List<Solicitud> solicitudesList = new ArrayList<>();
            Set<Integer> yaInsertados = new HashSet<>();

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
                    if (fields.length < 29 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {

                        Integer idSolicitud = conversores.parseIntSafe(fields[0]);
                        Integer tipoSolicitud = conversores.parseIntSafe(fields[1]);
                        Integer idTipoSolicitud2 = conversores.parseIntSafe(fields[2]);
                        Cliente cliente = iCliente.findById(conversores.parseLongSafe(fields[3])).orElse(null);
                        Integer idPerido = conversores.parseIntSafe(fields[4]);
                        Integer idPresentacion = conversores.parseIntSafe(fields[5]);
                        Date fecha = conversores.parseFecha(fields[6]);
                        String notas = fields[7].isBlank() ? null : fields[7];
                        String nombreSolicitud = fields[8].isBlank() ? null : fields[8];
                        String telefono = fields[11].isBlank() ? null : fields[11];
                        String cedula = fields[10].isBlank()  ? null : fields[10] ;
                        String correo = fields[13].isBlank() ? null : fields[13];
                        User user = iUserRepository.findById(conversores.parseLongSafe(fields[3])).orElse(null);

                        if (!solicidutesTrabajo.containsKey(idSolicitud) && !yaInsertados.contains(idSolicitud)) {
                            Solicitud nuevaSolicitud = Solicitud.builder()
                                    .idSolicitud(idSolicitud)
                                    .tipoSolicitud(tipoSolicitud)
                                    .idTipoSolicitud2(idTipoSolicitud2)
                                    .cliente(cliente)
                                    .idPerido(idPerido)
                                    .idPresentacion(idPresentacion)
                                    .fecha(fecha)
                                    .notas(notas)
                                    .nombreSolicitud(nombreSolicitud)
                                    .telefono(telefono)
                                    .cedula(cedula)
                                    .correo(correo)
                                    .user(user)
                                    .build();
                            solicitudesList.add(nuevaSolicitud);
                            yaInsertados.add(idSolicitud);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iSolicitud.saveAll(solicitudesList);

                response.put("message", "Importación completada. Total registros: " + solicitudesList.size());
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
