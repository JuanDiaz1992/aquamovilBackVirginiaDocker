package com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes.presolicitudes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
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
public class SolicitudServices {
    private final ISolicitud iSolicitud;
    private final JwtInterceptor jwtInterceptor;
    private final Conversores conversores;
    private final ICliente iCliente;
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final IProductosYServicios iProductosYServicios;
    private final ISubCotizaciones iSubCotizaciones;
    @Value("${user.photos.base.path}")
    private String USER_PHOTOS_BASE_PATH;

    public ResponseEntity<Map<String, Object>> getSolicitudTrabajo() {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = jwtInterceptor.getCurrentUser();
            List<Cliente> clientesList = new ArrayList<>();
            List<Map<String, Object>> solicitudesList = iSolicitud.findByUser(user)
                    .stream()
                    .map(solicitudTrabajo -> {
                        Map<String, Object> solicitud = new HashMap<>();
                        clientesList.add(solicitudTrabajo.getCliente());

                        // Solo los campos necesarios de la autorización
                        solicitud.put("idOt", solicitudTrabajo.getIdOt());
                        solicitud.put("idSolicitud", solicitudTrabajo.getIdSolicitud());
                        solicitud.put("tipoSolcitud", solicitudTrabajo.getTipoSolicitud());
                        solicitud.put("tipoSolicitud2", solicitudTrabajo.getIdTipoSolicitud2());
                        solicitud.put("idCliente", solicitudTrabajo.getCliente().getIdCliente());
                        solicitud.put("notas", solicitudTrabajo.getNotas());
                        solicitud.put("fecha", solicitudTrabajo.getFecha());
                        solicitud.put("nombreSolicitud", solicitudTrabajo.getNombreSolicitud());
                        solicitud.put("telefono", solicitudTrabajo.getTelefono());
                        solicitud.put("cedula", solicitudTrabajo.getCedula());
                        solicitud.put("correo", solicitudTrabajo.getCorreo());
                        solicitud.put("completado" , solicitudTrabajo.getCompletado());
                        solicitud.put("sincronizado", solicitudTrabajo.getSincronizado());
                        solicitud.put("firma" ,solicitudTrabajo.getFirma());
                        solicitud.put("notasOperario" , solicitudTrabajo.getNotasOperario());
                        solicitud.put("documentoDeQuienAtiende" , solicitudTrabajo.getDocumentoDeQuienAtiende());
                        return solicitud;
                    })
                    .toList();
            response.put("clientes",clientesList);
            response.put("solicitudes", solicitudesList);
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
    public ResponseEntity<Map<String, Object>> setSolicitudesServices(String solicitudesJson, MultipartFile[] photos){
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(solicitudesJson, new TypeReference<>() {});
            List<Map<String, Object>> listPresolicitudes = (List<Map<String, Object>>) jsonMap.get("presolicitudes");

            // 2. Indexar fotos por nombre de archivo para acceso rápido
            Map<String, MultipartFile> fotosIndexadas = indexarFotosPorNombre(photos);

            // 3. Procesar cada lectura
            for (Map<String, Object> presolicitudItem : listPresolicitudes) {
                if (isSincronizado(presolicitudItem)) continue;

                procesarPreSolicitud(presolicitudItem, user, fotosIndexadas);
            }

            response.put("status", 200);
            response.put("message", "Lecturas y fotos guardadas correctamente");
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

    private void procesarPreSolicitud(Map<String, Object> presolicitudItem, User user,
                                 Map<String, MultipartFile> fotosIndexadas) throws Exception {
        Long idCliente = conversores.getLongValue(presolicitudItem.get("fk_id_cliente"));
        List<MultimediaPreSolicitudes> fotosPreSolicitudes = procesarFotosDeSolicitud(presolicitudItem, idCliente, fotosIndexadas);

        // Construir y guardar
        Solicitud solicitud = construirEntidadSolicitud(presolicitudItem, idCliente, fotosPreSolicitudes);

        // Establecer relación inversa en fotos
        fotosPreSolicitudes.forEach(foto -> foto.setSolicitud(solicitud));

        if(solicitud !=null){
            iSolicitud.save(solicitud);
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

    private List<MultimediaPreSolicitudes> procesarFotosDeSolicitud(Map<String, Object> preSolicitudItem,
                                                                   Long idCliente,
                                                                   Map<String, MultipartFile> fotosIndexadas) {
        List<MultimediaPreSolicitudes> fotosLecturas = new ArrayList<>();
        List<Map<String, Object>> fotosJson = (List<Map<String, Object>>) preSolicitudItem.get("fotos");

        if (fotosJson != null && !fotosJson.isEmpty()) {
            for (Map<String, Object> fotoJson : fotosJson) {
                try {
                    String rutaFoto = (String) fotoJson.get("ruta_foto_solicitud");
                    if (rutaFoto != null) {
                        String nombreArchivo = extraerNombreArchivo(rutaFoto);
                        MultipartFile fotoArchivo = fotosIndexadas.get(nombreArchivo);

                        if (fotoArchivo != null) {
                            MultimediaPreSolicitudes fotoPreSolicitud = crearFotoPreSolicitud(fotoArchivo, fotoJson, idCliente);
                            fotosLecturas.add(fotoPreSolicitud);
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

    private MultimediaPreSolicitudes crearFotoPreSolicitud(MultipartFile fotoArchivo,
                                                       Map<String, Object> fotoJson,
                                                       Long idCliente) throws IOException, ParseException {
        // Guardar el archivo físico
        String rutaAlmacenada = guardarFotoEnServidor(fotoArchivo, idCliente);

        // Crear entidad FotosLecturas
        return MultimediaPreSolicitudes.builder()
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
    public Solicitud construirEntidadSolicitud(Map<String, Object> solicitudItem,
                                               Long idCliente,
                                               List<MultimediaPreSolicitudes> fotos) throws ParseException, JsonProcessingException {
        Cliente cliente = iCliente.findById(idCliente).orElse(null);
        if (solicitudItem.get("completado") !=null && conversores.convertirABoolean(solicitudItem.get("completado").toString())){
            log.info(solicitudItem.toString());
            Solicitud solicitud = iSolicitud.findById(conversores.getLongValue(solicitudItem.get("id_ot"))).orElse(null);
            solicitud.setCompletado(conversores.convertirABoolean(solicitudItem.get("completado").toString()));
            solicitud.setMultimediaPreSolicitudesList(fotos);
            solicitud.setFirma(solicitudItem.get("firma_cliente") != null ? solicitudItem.get("firma_cliente").toString():null);
            solicitud.setDocumentoDeQuienAtiende(solicitudItem.get("documento_de_quien_atendio") != null? solicitudItem.get("documento_de_quien_atendio").toString():null);
            solicitud.setNotasOperario(solicitudItem.get("observacion_operario") != null ? solicitudItem.get("observacion_operario").toString():null);
            solicitud.setSincronizado(true);
            AutorizacionTrabajo autorizacionTrabajoExist = iAutorizacionTrabajo.findById(conversores.getLongValue(solicitudItem.get("id_ot"))).orElse(null);
            if (autorizacionTrabajoExist == null) {
                AutorizacionTrabajo autorizacionTrabajo = AutorizacionTrabajo.builder()
                        .idSolicitud(solicitud.getIdSolicitud())
                        .cliente(cliente)
                        .idOt(solicitud.getIdOt())
                        .observacion(solicitudItem.get("observacion_operario") != null ? solicitudItem.get("observacion_operario").toString():null)
                        .notasSoicitud(solicitud.getNotas())
                        .vrCotizacion(solicitudItem.get("vr_OT") != null ? conversores.parseIntSafe(solicitudItem.get("vr_OT").toString()):null)
                        .vrOt(solicitudItem.get("vr_OT") != null ? conversores.parseIntSafe(solicitudItem.get("vr_OT").toString()):null)
                        .fecha(solicitud.getFecha())
                        .build();
                iAutorizacionTrabajo.save(autorizacionTrabajo);
                List<Map<String, Object>> subcotizacionesJson = (List<Map<String, Object>>) solicitudItem.get("subcotizaciones");
                try {
                    List<SubCotizaciones> subCotizacionesList = new ArrayList<>();
                    for (Map<String, Object> item : subcotizacionesJson) {
                        Long idProducto = conversores.getLongValue(item.get("fk_id_productos_y_servicios"));
                        ProductosYServicios productosYServicio = iProductosYServicios.findById(idProducto).orElse(null);

                        if (productosYServicio == null) {
                            log.warn("Producto no encontrado con ID: " + idProducto);
                            continue;
                        }

                        SubCotizaciones subCotizaciones = SubCotizaciones.builder()
                                .idCotizacion(solicitud.getIdOt())
                                .autorizacionTrabajo(autorizacionTrabajo)
                                .valor(item.get("valor") != null ? conversores.parseDoubleSafe(item.get("valor").toString()) : null)
                                .vrUnitario(productosYServicio.getValorUnitario())
                                .idServicio(item.get("id_servicio") != null ? conversores.parseIntSafe(item.get("id_servicio").toString()) : null)
                                .cantidad(item.get("cantidad") != null ? conversores.parseIntSafe(item.get("cantidad").toString()) : null)
                                .productosYServicios(productosYServicio)
                                .asume(item.get("asume") != null ? conversores.parseIntSafe(item.get("asume").toString()) : null)
                                .build();
                        subCotizacionesList.add(subCotizaciones);
                    }

                    iSubCotizaciones.saveAll(subCotizacionesList);
                    cliente.setPreSolicitudCompletada(true);
                    iCliente.save(cliente);
                    log.info("Subcotizaciones guardadas correctamente: " + subCotizacionesList.size());
                } catch (Exception e) {
                    log.error("Error al guardar subcotizaciones", e);
                }
                return solicitud;
            }
        }

    return null;
    }

}
