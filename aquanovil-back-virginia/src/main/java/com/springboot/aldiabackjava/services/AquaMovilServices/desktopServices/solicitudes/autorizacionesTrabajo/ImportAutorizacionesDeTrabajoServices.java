package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.SubCotizaciones;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.IAutorizacionTrabajo;
import com.springboot.aldiabackjava.repositories.solicitudes.IProductosYServicios;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.ISubCotizaciones;
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
public class ImportAutorizacionesDeTrabajoServices {
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final ISubCotizaciones iSubCotizaciones;
    private final ICliente iCliente;
    private final IUserRepository iUserRepository;
    private final Conversores conversores;
    private final IProductosYServicios iProductosYServicios;

    public ResponseEntity<Map<String,Object>> getAutorizacionesTrabajoServices(MultipartFile file){
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, AutorizacionTrabajo> autorizacionTrabajoMap = iAutorizacionTrabajo.findAll().stream()
                    .collect(Collectors.toMap(AutorizacionTrabajo::getIdOt, c -> c));
            List<AutorizacionTrabajo> autorizacionTrabajosList = new ArrayList<>();
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
                    if (fields.length < 13 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {

                        Long idOt = conversores.parseLongSafe(fields[0]);
                        Integer idVt = conversores.parseIntSafe(fields[1]);
                        Integer idSolicitud = conversores.parseIntSafe(fields[2]);
                        Cliente cliente = iCliente.findById(conversores.parseLongSafe(fields[3])).orElse(null);
                        Date fecha = conversores.parseFecha(fields[4]);
                        String observacion = fields[5];
                        Integer vrCotizcion = conversores.parseIntSafe(fields[6]);
                        User user = iUserRepository.findById(conversores.parseLongSafe(fields[9])).orElse(null);
                        Integer vrOt = conversores.parseIntSafe(fields[10]);
                        if (!autorizacionTrabajoMap.containsKey(idOt) && !yaInsertados.contains(idOt)) {
                            AutorizacionTrabajo nuevaAutorizacion = AutorizacionTrabajo.builder()
                                    .idOt(idOt)
                                    .idVt(idVt)
                                    .idSolicitud(idSolicitud)
                                    .cliente(cliente)
                                    .fecha(fecha)
                                    .observacion(observacion)
                                    .vrCotizacion(vrCotizcion)
                                    .user(user)
                                    .vrOt(vrOt)
                                    .build();
                            autorizacionTrabajosList.add(nuevaAutorizacion);
                            yaInsertados.add(idOt);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iAutorizacionTrabajo.saveAll(autorizacionTrabajosList);

                response.put("message", "Importación completada. Total registros: " + autorizacionTrabajosList.size());
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

    public ResponseEntity<Map<String,Object>> getSubCotizaciones(MultipartFile file){
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, SubCotizaciones> subCotizacionesMap = iSubCotizaciones.findAll().stream()
                    .collect(Collectors.toMap(SubCotizaciones::getIdCotizacion, c -> c));
            List<SubCotizaciones> subCotizacionesList = new ArrayList<>();

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
                    if (fields.length < 17 || fields[0].isBlank() || fields[1].isBlank()) {
                        log.warn("Línea inválida o incompleta: {}", Arrays.toString(fields));
                        continue;
                    }

                    try {
                        AutorizacionTrabajo autorizacionTrabajo = iAutorizacionTrabajo.findById(conversores.parseLongSafe(fields[0])).orElse(null);
                        ProductosYServicios productosYServicios = iProductosYServicios.findById(conversores.parseLongSafe(fields[1])).orElse(null);
                        Integer cantidad = conversores.parseIntSafe(fields[3]);
                        Double vrUnitario = conversores.parseDoubleSafe(fields[4]);
                        Double valor = conversores.parseDoubleSafe(fields[5]);
                        Double valorAnterior = conversores.parseDoubleSafe(fields[6]);
                        String descripcion = fields[7];
                        Integer idCargo = conversores.parseIntSafe(fields[8]);
                        Integer idServicio = conversores.parseIntSafe(fields[9]);
                        Integer asume = conversores.parseIntSafe(fields[18]);

                        SubCotizaciones nuevaSubCotizacion = SubCotizaciones.builder()
                                .autorizacionTrabajo(autorizacionTrabajo)
                                .productosYServicios(productosYServicios)
                                .cantidad(cantidad)
                                .valor(valor)
                                .vrUnitario(vrUnitario)
                                .valorAnterior(valorAnterior)
                                .descripcion(descripcion)
                                .idCargo(idCargo)
                                .idServicio(idServicio)
                                .asume(asume)
                                .build();
                        subCotizacionesList.add(nuevaSubCotizacion);

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iSubCotizaciones.saveAll(subCotizacionesList);

                response.put("message", "Importación completada. Total registros: " + subCotizacionesList.size());
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
