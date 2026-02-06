package com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.autorizacionesTrabajo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.MultimediaAutorizaciones;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.SubCotizaciones;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.MultimediaPreSolicitudes;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.Solicitud;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.solicitudes.IProductosYServicios;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.IAutorizacionTrabajo;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.ISubCotizaciones;
import com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes.ISolicitud;
import com.springboot.aldiabackjava.utils.Conversores;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutorizacionServices {
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final ISubCotizaciones iSubCotizaciones;
    private final ISolicitud iSolicitud;
    private final JwtInterceptor jwtInterceptor;
    private final Conversores conversores;
    private final ICliente iCliente;
    private final IProductosYServicios iProductosYServicios;
    @Value("${user.photos.base.path}")
    private String USER_PHOTOS_BASE_PATH;

    public ResponseEntity<Map<String, Object>> getAutorizacionesrabajo() {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = jwtInterceptor.getCurrentUser();
            List<Cliente> clientesList = new ArrayList<>();
            List<Map<String, Object>> subCotizaciones = new ArrayList<>();
            List<Map<String, Object>> solicitudesList = iAutorizacionTrabajo.findByUser(user)
                    .stream()
                    .map(autorizacion -> {
                        Map<String, Object> autorizaciones = new HashMap<>();
                        clientesList.add(autorizacion.getCliente());
                        // Subcotizaciones simplificadas
                        List<Object[]> subcotizacionesRaw = iSubCotizaciones.findReducidasByAutorizacion(autorizacion);

                        subcotizacionesRaw.forEach(row -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("idOt", row[0]);
                            map.put("idItem", row[1]);
                            map.put("idCotizacion", row[2]);
                            map.put("cantidad", row[3]);
                            map.put("vrUnitario", row[4]);
                            map.put("valor", row[5]);
                            map.put("valorAnterior", row[6]);
                            map.put("descripcion", row[7]);
                            map.put("idCargo", row[8]);
                            map.put("idServicio", row[9]);
                            map.put("asume", row[10]);
                            subCotizaciones.add(map);
                        });
                        Solicitud solicitud = iSolicitud.findById(autorizacion.getIdOt()).orElse(null);
                        // Solo los campos necesarios de la autorización
                        autorizaciones.put("idOt", autorizacion.getIdOt());
                        autorizaciones.put("idVt", autorizacion.getIdVt());
                        autorizaciones.put("idSolicitud", autorizacion.getIdSolicitud());
                        autorizaciones.put("idCliente", autorizacion.getCliente().getIdCliente());
                        autorizaciones.put("fecha", autorizacion.getFecha());
                        autorizaciones.put("observacion", autorizacion.getObservacion());
                        autorizaciones.put("notasCliente", solicitud.getNotas());
                        autorizaciones.put("telefono", solicitud.getTelefono());
                        autorizaciones.put("correo", solicitud.getCorreo());
                        autorizaciones.put("cedula", solicitud.getCedula());
                        autorizaciones.put("tipoSolicitud", solicitud.getTipoSolicitud());
                        autorizaciones.put("claseSolicitud", solicitud.getIdTipoSolicitud2());
                        autorizaciones.put("vrCotizacion", autorizacion.getVrCotizacion());
                        autorizaciones.put("vrOt", autorizacion.getVrOt());
                        autorizaciones.put("sincronizado", autorizacion.getSincronizado());
                        autorizaciones.put("fechaRealizacion", autorizacion.getFechaRealizacion());
                        autorizaciones.put("notasOperario", autorizacion.getNotasOperario());
                        autorizaciones.put("documentoDeQuienAtiende", autorizacion.getDocumentoDeQuienAtiende());
                        autorizaciones.put("firma", autorizacion.getFirma());
                        autorizaciones.put("fechaAsignacion", autorizacion.getFechaDeAsignacion());
                        autorizaciones.put("status", autorizacion.getStatus());
                        if (!autorizacion.getHistorial().isEmpty()){
                            List<Map<String,Object>> historialAutorizaciones = autorizacion.getHistorial().stream()
                                    .map(historial -> {
                                        Map<String, Object> historialMap = new HashMap<>();
                                        historialMap.put("idHistorial", historial.getId());
                                        historialMap.put("fkIdUser", historial.getUser().getIdUser());
                                        historialMap.put("nombreUser", historial.getUser().getName());
                                        historialMap.put("fkIdAutorizacion", autorizacion.getIdOt());
                                        historialMap.put("observaciones", historial.getObservaciones());
                                        historialMap.put("fecha", historial.getCreatedAt());
                                        return historialMap;
                                    }
                                    ).toList();
                            autorizaciones.put("historial", historialAutorizaciones);
                        }
                        return autorizaciones;
                    })
                    .toList();
            response.put("clientes",clientesList);
            response.put("solicitudes", solicitudesList);
            response.put("subCotizaciones", subCotizaciones);
            if (solicitudesList.isEmpty()){
                response.put("message", "No hay solicitudes pendientes");
                response.put("status", 200);
                return ResponseEntity.ok(response);
            }
            response.put("status", 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error al realizar la consulta, intentelo más tarde");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> setAutorizacionesServices(String autorizacionesJson, MultipartFile[] photos){
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(autorizacionesJson, new TypeReference<>() {});
            List<Map<String, Object>> listAutorizaciones = (List<Map<String, Object>>) jsonMap.get("presolicitudes");

            // 2. Indexar fotos por nombre de archivo para acceso rápido
            Map<String, MultipartFile> fotosIndexadas = indexarFotosPorNombre(photos);

            // 3. Procesar cada lectura
            for (Map<String, Object> autorizacionItem : listAutorizaciones) {
                if (isSincronizado(autorizacionItem)) continue;

                procesaAutorizacion(autorizacionItem, user, fotosIndexadas);
            }

            response.put("status", 200);
            response.put("message", "Autorizaciones y fotos guardadas correctamente");
        } catch (Exception e) {
            log.error("Error en setSolicitudesServices: {}", e.getMessage(), e);
            response.put("status", 500);
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok().body(response);
    }

    private Map<String, MultipartFile> indexarFotosPorNombre(MultipartFile[] multimedia) {
        Map<String, MultipartFile> index = new HashMap<>();
        if (multimedia != null) {
            for (MultipartFile photo : multimedia) {
                if (photo != null && photo.getOriginalFilename() != null) {
                    index.put(photo.getOriginalFilename(), photo);
                }
            }
        }
        return index;
    }

    private void procesaAutorizacion(Map<String, Object> autorizacionItem, User user,
                                      Map<String, MultipartFile> fotosIndexadas) throws Exception {
        Long idCliente = conversores.getLongValue(autorizacionItem.get("fk_id_cliente"));
        List<MultimediaAutorizaciones> fotosAutorizaciones = procesarFotoAutorizacion(autorizacionItem, idCliente, fotosIndexadas);

        // Construir y guardar
        AutorizacionTrabajo autorizacionTrabajo = construirEntidadAutorizacio(autorizacionItem, idCliente, fotosAutorizaciones);

        // Establecer relación inversa en fotos
        fotosAutorizaciones.forEach(foto -> foto.setAutorizacionTrabajo(autorizacionTrabajo));

        if(autorizacionTrabajo !=null){
            iAutorizacionTrabajo.save(autorizacionTrabajo);
        }

    }


    private boolean isSincronizado(Map<String, Object> lecturaItem) {
        Object sincronizadoObj = lecturaItem.get("sincronizado");
        return (sincronizadoObj instanceof Boolean && (Boolean) sincronizadoObj) ||
                (sincronizadoObj instanceof Number && ((Number) sincronizadoObj).intValue() == 1);
    }


    private Date parseFecha(String fechaStr) throws ParseException {
        if (fechaStr == null || fechaStr.isEmpty()) {
            return null;
        }

        try {
            // Intenta parsear formato ISO con zona horaria (ej: "2025-03-26T20:19:54.912Z")
            Instant instant = Instant.parse(fechaStr);
            return Date.from(instant);
        } catch (DateTimeParseException e1) {
            try {
                // Intenta parsear formato SQL (ej: "2025-03-26 15:20:01")
                SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sqlFormat.parse(fechaStr);
            } catch (ParseException e2) {
                throw new ParseException("Formato de fecha no reconocido: " + fechaStr, 0);
            }
        }
    }

    private List<MultimediaAutorizaciones> procesarFotoAutorizacion(Map<String, Object> autorizacionItem,
                                                                    Long idCliente,
                                                                    Map<String, MultipartFile> fotosIndexadas) {
        List<MultimediaAutorizaciones> fotosLecturas = new ArrayList<>();
        List<Map<String, Object>> fotosJson = (List<Map<String, Object>>) autorizacionItem.get("fotos");

        if (fotosJson != null && !fotosJson.isEmpty()) {
            for (Map<String, Object> fotoJson : fotosJson) {
                try {
                    String rutaFoto = (String) fotoJson.get("ruta_foto_solicitud");
                    if (rutaFoto != null) {
                        String nombreArchivo = extraerNombreArchivo(rutaFoto);
                        MultipartFile fotoArchivo = fotosIndexadas.get(nombreArchivo);

                        if (fotoArchivo != null) {
                            MultimediaAutorizaciones fotoAutorizacion = crearFotoAutorizacion(fotoArchivo, fotoJson, idCliente);
                            fotosLecturas.add(fotoAutorizacion);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al procesar foto: {}", e.getMessage(), e);
                    // Continuar con las siguientes fotos aunque falle una
                }
            }
        }
        return fotosLecturas;
    }

    private String extraerNombreArchivo(String rutaCompleta) {
        return rutaCompleta.substring(rutaCompleta.lastIndexOf('/') + 1);
    }

    private MultimediaAutorizaciones crearFotoAutorizacion(MultipartFile fotoArchivo,
                                                           Map<String, Object> fotoJson,
                                                           Long idCliente) throws IOException, ParseException {
        // Guardar el archivo físico
        String rutaAlmacenada = guardarFotoEnServidor(fotoArchivo, idCliente);

        // Crear entidad FotosLecturas
        return MultimediaAutorizaciones.builder()
                .rutaFotoSolicitud(rutaAlmacenada)
                .fecha(parseFecha((String) fotoJson.get("fecha")))
                .cliente(iCliente.findById(idCliente).orElse(null))
                .build();
    }

    private String guardarFotoEnServidor(MultipartFile foto, Long idCliente) throws IOException {
        // 1. Construir estructura de directorios
        String clienteFolder = "cliente_" + idCliente + "/";
        String uploadDir = USER_PHOTOS_BASE_PATH + clienteFolder;

        // 2. Asegurar que existe el directorio
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Obtener nombre original del archivo
        String nombreArchivo = foto.getOriginalFilename();

        // 4. Guardar el archivo
        Path filePath = uploadPath.resolve(nombreArchivo);
        try (InputStream inputStream = foto.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 5. Retornar ruta relativa (cliente_6/nombrearchivo.jpg)
        return clienteFolder + nombreArchivo;
    }
    @Transactional
    public AutorizacionTrabajo construirEntidadAutorizacio(Map<String, Object> autorizacionItem,
                                               Long idCliente,
                                               List<MultimediaAutorizaciones> fotos) throws ParseException, JsonProcessingException {
        Cliente cliente = iCliente.findById(idCliente).orElse(null);
        log.info(autorizacionItem.toString());
        if (autorizacionItem.get("completado") !=null && conversores.convertirABoolean(autorizacionItem.get("completado").toString())){
            log.info(autorizacionItem.toString());
            AutorizacionTrabajo autorizacionTrabajo = iAutorizacionTrabajo.findById(conversores.getLongValue(autorizacionItem.get("id_ot"))).orElse(null);
            autorizacionTrabajo.setStatus(AutorizacionTrabajo.WorkOrderStatus.valueOf(autorizacionItem.get("completado").toString().toUpperCase()));
            autorizacionTrabajo.setMultimediaAutorizaciones(fotos);
            autorizacionTrabajo.setNotasOperario(autorizacionItem.get("observacion_operario") != null ? autorizacionItem.get("observacion_operario").toString() : null);
            autorizacionTrabajo.setFirma(autorizacionItem.get("firma_cliente") != null ? autorizacionItem.get("firma_cliente").toString():null);
            autorizacionTrabajo.setVrOt(conversores.parseIntSafe(autorizacionItem.get("vr_OT").toString()));
            autorizacionTrabajo.setDocumentoDeQuienAtiende(autorizacionItem.get("documento_de_quien_atendio") != null? autorizacionItem.get("documento_de_quien_atendio").toString():null);
            autorizacionTrabajo.setFechaRealizacion(conversores.parseFecha(autorizacionItem.get("fecha_realizacion").toString()));
            autorizacionTrabajo.setSincronizado(true);
            List<Map<String, Object>> subcotizacionesJson = (List<Map<String, Object>>) autorizacionItem.get("subcotizaciones");
                try {
                    List<SubCotizaciones> subCotizacionesList = new ArrayList<>();
                    for (Map<String, Object> item : subcotizacionesJson) {

                        SubCotizaciones subCotizacionExist = iSubCotizaciones.findById(conversores.getLongValue(item.get("id_cotizacion"))).orElse(null);
                        if(subCotizacionExist != null) {
                            subCotizacionExist.setAsume(item.get("asume") != null ? conversores.parseIntSafe(item.get("asume").toString()) : null);
                            subCotizacionExist.setCantidad(item.get("cantidad") != null ? conversores.parseIntSafe(item.get("cantidad").toString()) : null);
                            subCotizacionExist.setIdServicio(item.get("id_servicio") != null ? conversores.parseIntSafe(item.get("id_servicio").toString()) : null);
                            subCotizacionExist.setValorAnterior(subCotizacionExist.getValor());
                            subCotizacionExist.setValor(item.get("valor") != null ? conversores.parseDoubleSafe(item.get("valor").toString()) : null);
                            subCotizacionExist.setDocumentoOperario(item.get("documento_operario") != null ? conversores.parseIntSafe(item.get("documento_operario").toString()) : null);
                            subCotizacionesList.add(subCotizacionExist);
                        }else{
                            Long idProducto = conversores.getLongValue(item.get("fk_id_productos_y_servicios"));
                            ProductosYServicios productosYServicio = iProductosYServicios.findById(idProducto).orElse(null);

                            if (productosYServicio == null) {
                                log.warn("Producto no encontrado con ID: " + idProducto);
                                continue;
                            }

                            SubCotizaciones subCotizaciones = SubCotizaciones.builder()
                                    .idCotizacion(autorizacionTrabajo.getIdOt())
                                    .autorizacionTrabajo(autorizacionTrabajo)
                                    .valor(item.get("valor") != null ? conversores.parseDoubleSafe(item.get("valor").toString()) : null)
                                    .vrUnitario(productosYServicio.getValorUnitario())
                                    .idServicio(item.get("id_servicio") != null ? conversores.parseIntSafe(item.get("id_servicio").toString()) : null)
                                    .cantidad(item.get("cantidad") != null ? conversores.parseIntSafe(item.get("cantidad").toString()) : null)
                                    .productosYServicios(productosYServicio)
                                    .asume(item.get("asume") != null ? conversores.parseIntSafe(item.get("asume").toString()) : null)
                                    .documentoOperario(item.get("documento_operario") != null ? conversores.parseIntSafe(item.get("documento_operario").toString()) : null)
                                    .build();
                            subCotizacionesList.add(subCotizaciones);
                        }

                    }

                    iSubCotizaciones.saveAll(subCotizacionesList);
                    cliente.setAutorizacionTrabajoCompletada(true);
                    iCliente.save(cliente);
                    log.info("Subcotizaciones guardadas correctamente: " + subCotizacionesList.size());
                } catch (Exception e) {
                    log.error("Error al guardar subcotizaciones", e);
                }
                return autorizacionTrabajo;
            }
        return null;
    }

}
