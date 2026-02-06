package com.springboot.aldiabackjava.services.AquaMovilServices.criticas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturasCriticas.CriticaLectura;
import com.springboot.aldiabackjava.models.lecturasCriticas.MultimediaCriticaLecturas;
import com.springboot.aldiabackjava.models.lecturasCriticas.SolicitudLecturaCritica;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ICriticaLectura;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Service
@RequiredArgsConstructor
public class CriticaLecturaServices {
    private final JwtInterceptor jwtInterceptor;
    private final ICliente iCliente;
    private final ICriticaLectura iCriticaLectura;
    private final ISolicitudLecturaCritica iSolicitudLecturaCritica;
    @Value("${user.photos.base.path}")
    private String USER_PHOTOS_BASE_PATH;


    private Long getLongValue(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).longValue() : null;
    }

    private Integer getIntValue(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).intValue() : null;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> setCriticas(String lecturasJson, MultipartFile[] photos) {
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 1. Deserializar el JSON completo
            Map<String, Object> jsonMap = objectMapper.readValue(lecturasJson, new TypeReference<>() {});
            List<Map<String, Object>> listaCriticas = (List<Map<String, Object>>) jsonMap.get("criticas");

            // 2. Indexar fotos por nombre de archivo para acceso rápido
            Map<String, MultipartFile> fotosIndexadas = indexarFotosPorNombre(photos);

            // 3. Procesar cada lectura
            for (Map<String, Object> criticasItem : listaCriticas) {
                if (isSincronizado(criticasItem)) continue;

                procesarLectura(criticasItem, user, fotosIndexadas);
            }

            response.put("status", 200);
            response.put("message", "Lecturas y fotos guardadas correctamente");
        } catch (Exception e) {
            log.error("Error en setLecturas: {}", e.getMessage(), e);
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

    private void procesarLectura(Map<String, Object> criticaItem, User user,
                                 Map<String, MultipartFile> fotosIndexadas) throws Exception {
        Long idCliente = getLongValue(criticaItem.get("fk_id_cliente"));
        List<MultimediaCriticaLecturas> fotosCriticas = procesarFotosDeCritica(criticaItem, idCliente, fotosIndexadas);

        // Construir y guardar la entidad Lectura
        CriticaLectura criticaLectura = construirEntidadCritica(criticaItem, user, idCliente, fotosCriticas);

        // Establecer relación inversa en fotos
        fotosCriticas.forEach(foto -> foto.setCriticaLectura(criticaLectura));

        iCriticaLectura.save(criticaLectura);
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

    private List<MultimediaCriticaLecturas> procesarFotosDeCritica(Map<String, Object> criticaItem,
                                                            Long idCliente,
                                                            Map<String, MultipartFile> fotosIndexadas) {
        List<MultimediaCriticaLecturas> fotosLecturas = new ArrayList<>();
        List<Map<String, Object>> fotosJson = (List<Map<String, Object>>) criticaItem.get("fotos");

        if (fotosJson != null && !fotosJson.isEmpty()) {
            for (Map<String, Object> fotoJson : fotosJson) {
                try {
                    String rutaFoto = (String) fotoJson.get("ruta_foto_critica");
                    if (rutaFoto != null) {
                        String nombreArchivo = extraerNombreArchivo(rutaFoto);
                        MultipartFile fotoArchivo = fotosIndexadas.get(nombreArchivo);

                        if (fotoArchivo != null) {
                            MultimediaCriticaLecturas fotoCritica = crearFotoCritica(fotoArchivo, fotoJson, idCliente);
                            fotosLecturas.add(fotoCritica);
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

    private MultimediaCriticaLecturas crearFotoCritica(MultipartFile fotoArchivo,
                                                Map<String, Object> fotoJson,
                                                Long idCliente) throws IOException, ParseException {
        // Guardar el archivo físico
        String rutaAlmacenada = guardarFotoEnServidor(fotoArchivo, idCliente);

        // Crear entidad FotosLecturas
        return MultimediaCriticaLecturas.builder()
                .rutaFotoLectura(rutaAlmacenada)
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

    private CriticaLectura construirEntidadCritica(Map<String, Object> criticaItem,
                                            User user,
                                            Long idCliente,
                                            List<MultimediaCriticaLecturas> fotos) throws ParseException, JsonProcessingException {
        Cliente cliente = iCliente.findById(idCliente).orElse(null);
        SolicitudLecturaCritica solicitudLecturaCritica = iSolicitudLecturaCritica.findById(getLongValue(criticaItem.get("fk_id_solicitud_critica_lectura"))).orElse(null);

        // Buscar si ya existe una critca con ese ID externo
        Optional<CriticaLectura> criticaExistente = Optional.ofNullable(iCriticaLectura.findByCliente(cliente));

        // Si existe, actualizamos la lectura existente
        if (criticaExistente.isPresent()) {
            CriticaLectura criticaLectura = criticaExistente.get();

            // Actualizar campos modificables
            criticaLectura.setLectura(getIntValue(criticaItem.get("lectura")));
            criticaLectura.setAtendio((String) criticaItem.get("atendido"));
            criticaLectura.setDocumentoAntendio((String) criticaItem.get("documento_atendio"));
            criticaLectura.setTelefono((String) criticaItem.get("telefono"));
            criticaLectura.setObservaciones((String) criticaItem.get("observaciones"));
            criticaLectura.setRespuestas((String) criticaItem.get("respuestas"));
            criticaLectura.setFecha(parseFecha((String) criticaItem.get("fecha")));
            criticaLectura.setMultimedia(fotos);
            criticaLectura.setUser(user);

            return criticaLectura;
        }
        // Si no existe, creamos una nueva lectura
        else {
            if (cliente != null) {
                cliente.setCriticaLecturaCompletada(true);
                iCliente.save(cliente);
            }
            solicitudLecturaCritica.setCompletada(true);
            iSolicitudLecturaCritica.save(solicitudLecturaCritica);
            Object rawRespuestas = criticaItem.get("respuestas");

            String respuestas;

            if (rawRespuestas instanceof String) {
                respuestas = (String) rawRespuestas;
            } else {
                respuestas = new ObjectMapper().writeValueAsString(rawRespuestas);
            }
            log.info(respuestas);
            return CriticaLectura.builder()
                    .lectura(getIntValue(criticaItem.get("lectura")))
                    .atendio((String) criticaItem.get("atendido"))
                    .documentoAntendio((String) criticaItem.get("documento_atendio"))
                    .telefono((String) criticaItem.get("telefono"))
                    .observaciones((String) criticaItem.get("observaciones"))
                    .respuestas(respuestas)
                    .fecha(parseFecha((String) criticaItem.get("fecha")))
                    .multimedia(fotos)
                    .user(user)
                    .cliente(cliente)
                    .solicitudLecturaCritica(solicitudLecturaCritica)
                    .motivo(solicitudLecturaCritica.getMotivo())
                    .build();
        }
    }


}
