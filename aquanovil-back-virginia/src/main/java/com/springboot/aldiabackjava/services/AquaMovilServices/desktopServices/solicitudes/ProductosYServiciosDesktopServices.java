package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes;

import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.repositories.solicitudes.IProductosYServicios;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductosYServiciosDesktopServices {
    private final IProductosYServicios iProductosYServicios;
    private final Conversores conversores;

    @Transactional
    public ResponseEntity<Map<String, Object>> importProductosYServiciosService(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "El archivo está vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            log.info("Precargando archivo");

            Map<Long, ProductosYServicios> productosYServiciosMap = iProductosYServicios.findAll().stream()
                    .collect(Collectors.toMap(ProductosYServicios::getIdItem, c -> c));
            List<ProductosYServicios> ProductosYServiciosList = new ArrayList<>();
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
                        Long idItem = conversores.parseLongSafe(fields[0]);
                        String codigo = fields[1];
                        String nombre = fields[2];
                        String unidad = fields[3];
                        Double valorU = conversores.parseDoubleSafe(fields[4]);
                        Integer gravado = conversores.parseIntSafe(fields[5]);
                        Integer idServicio = conversores.parseIntSafe(fields[6]);
                        Integer idCargo = conversores.parseIntSafe(fields[7]);
                        Integer idCargoAc = conversores.parseIntSafe(fields[8]);
                        Integer idCargoAi = conversores.parseIntSafe(fields[9]);
                        Integer idCargoAs = conversores.parseIntSafe(fields[10]);
                        Date fecha = conversores.parseFecha(fields[11]);
                        Boolean saleAlmacen = conversores.convertirABoolean(fields[12]);
                        Boolean activo = conversores.convertirABoolean(fields[13]);


                        if (!productosYServiciosMap.containsKey(idItem) && !yaInsertados.contains(idItem)) {
                            ProductosYServicios nuevoProductoYServicio = ProductosYServicios.builder()
                                    .idItem(idItem)
                                    .codigo(codigo)
                                    .nombreItem(nombre)
                                    .unidad(unidad)
                                    .valorUnitario(valorU)
                                    .gravado(gravado)
                                    .idServicio(idServicio)
                                    .idCargo(idCargo)
                                    .idCargoAc(idCargoAc)
                                    .idCargoAI(idCargoAi)
                                    .idCargoAs(idCargoAs)
                                    .fechaActualizacion(fecha)
                                    .saleAlmacen(saleAlmacen)
                                    .activo(activo)
                                    .build();
                            ProductosYServiciosList.add(nuevoProductoYServicio);
                            yaInsertados.add(idItem);
                        }

                    } catch (Exception e) {
                        log.error("Error procesando línea: {}", line, e);
                    }
                }

                iProductosYServicios.saveAll(ProductosYServiciosList);

                response.put("message", "Importación completada. Total registros: " + ProductosYServiciosList.size());
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

    public ResponseEntity<Map<String, Object>> deleteAllProductosYServiciosService() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductosYServicios> productosYServicios = iProductosYServicios.findAll();
            if (productosYServicios.isEmpty()) {
                response.put("status", 404);
                response.put("message", "La tabla de productos y servicios está vacía");
                return ResponseEntity.ok(response);
            }
            iProductosYServicios.deleteAll();
            response.put("status", 200);
            response.put("message", "Todos los productos y servicios han sido eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("error", "No se pueden eliminar los productos y servicios porque existen registros relacionados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error inesperado al eliminar los productos y servicios.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
