package com.springboot.aldiabackjava.services.AquaMovilServices.lecturas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.lecturas.MultimediaLecturas;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IObservaciones;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IParametros;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturas.ILecturas;
import com.springboot.aldiabackjava.repositories.ICiclo;
import com.springboot.aldiabackjava.repositories.rutasLecturas.ICrc;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class LecturasServices {
    private final JwtInterceptor jwtInterceptor;
    private final ICausal iCausal;
    private final IObservaciones iObservaciones;
    private final ICliente iCliente;
    private final IParametros iParametros;
    private final ILecturas ilecturas;
    private final IRuta iRuta;
    private final ICrc iCrc;
    private final ICiclo iCiclo;
    @Value("${user.photos.base.path}")
    private String USER_PHOTOS_BASE_PATH;

    private Long getLongValue(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).longValue() : null;
    }

    private Integer getIntValue(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).intValue() : null;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> setLecturas(String lecturasJson, MultipartFile[] photos, String rutasCompletadas) {
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (rutasCompletadas != null) {
                List<Map<String, Object>> listRoutesComplete = objectMapper.readValue(
                        rutasCompletadas,
                        new TypeReference<>() {
                        }
                );

                List<Ruta> rutas = iRuta.findAll();
                for (Map<String, Object> routeMap : listRoutesComplete) {
                    Long idRuta = Long.valueOf(routeMap.get("id_ruta").toString());
                    rutas.stream()
                            .filter(ruta -> ruta.getIdRuta().equals(idRuta))
                            .findFirst()
                            .ifPresent(ruta -> {
                                ruta.setEstado(true);
                                iRuta.save(ruta);
                            });
                }
            }


            // 1. Deserializar el JSON completo
            Map<String, Object> jsonMap = objectMapper.readValue(lecturasJson, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> listaDeLecturas = (List<Map<String, Object>>) jsonMap.get("lecturas");

            // 2. Indexar fotos por nombre de archivo para acceso rápido
            Map<String, MultipartFile> fotosIndexadas = indexarFotosPorNombre(photos);

            // 3. Procesar cada lectura
            for (Map<String, Object> lecturaItem : listaDeLecturas) {
                if (isSincronizado(lecturaItem)) continue;

                procesarLectura(lecturaItem, user, fotosIndexadas);
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

    private Map<String, MultipartFile> indexarFotosPorNombre(MultipartFile[] photos) {
        Map<String, MultipartFile> index = new HashMap<>();
        if (photos != null) {
            for (MultipartFile photo : photos) {
                if (photo != null && photo.getOriginalFilename() != null) {
                    index.put(photo.getOriginalFilename(), photo);
                }
            }
        }
        return index;
    }

    private void procesarLectura(Map<String, Object> lecturaItem, User user,
                                 Map<String, MultipartFile> fotosIndexadas) throws Exception {
        Long idCliente = getLongValue(lecturaItem.get("fk_id_cliente"));
        List<MultimediaLecturas> fotosLecturas = procesarFotosDeLectura(lecturaItem, idCliente, fotosIndexadas);

        // Construir y guardar la entidad Lectura
        Lectura lectura = construirEntidadLectura(lecturaItem, user, idCliente, fotosLecturas);

        // Establecer relación inversa en fotos
        fotosLecturas.forEach(foto -> foto.setLectura(lectura));

        ilecturas.save(lectura);
    }


    private Causal getCausal(Map<String, Object> lecturaItem) {
        Long idCausal = getLongValue(lecturaItem.get("fk_id_causal"));
        return (idCausal != null) ? iCausal.findById(idCausal).orElse(null) : null;
    }

    private Observaciones getObservacion(Map<String, Object> lecturaItem, String key) {
        Long idObs = getLongValue(lecturaItem.get(key));
        return (idObs != null) ? iObservaciones.findById(idObs).orElse(null) : null;
    }

    private Parametros getParametros(Map<String, Object> lecturaItem) {
        Long nivelAlerta = getLongValue(lecturaItem.get("nivel_alerta"));
        return (nivelAlerta != null) ? iParametros.findById(nivelAlerta).orElse(null) : null;
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

    private List<MultimediaLecturas> procesarFotosDeLectura(Map<String, Object> lecturaItem,
                                                            Long idCliente,
                                                            Map<String, MultipartFile> fotosIndexadas) {
        List<MultimediaLecturas> fotosLecturas = new ArrayList<>();
        List<Map<String, Object>> fotosJson = (List<Map<String, Object>>) lecturaItem.get("fotos");

        if (fotosJson != null && !fotosJson.isEmpty()) {
            for (Map<String, Object> fotoJson : fotosJson) {
                try {
                    String rutaFoto = (String) fotoJson.get("ruta_foto_lectura");
                    if (rutaFoto != null) {
                        String nombreArchivo = extraerNombreArchivo(rutaFoto);
                        MultipartFile fotoArchivo = fotosIndexadas.get(nombreArchivo);

                        if (fotoArchivo != null) {
                            MultimediaLecturas fotoLectura = crearFotoLectura(fotoArchivo, fotoJson, idCliente);
                            fotosLecturas.add(fotoLectura);
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

    private MultimediaLecturas crearFotoLectura(MultipartFile fotoArchivo,
                                                Map<String, Object> fotoJson,
                                                Long idCliente) throws IOException, ParseException {
        // Guardar el archivo físico
        String rutaAlmacenada = guardarFotoEnServidor(fotoArchivo, idCliente);

        // Crear entidad FotosLecturas
        return MultimediaLecturas.builder()
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

    private Lectura construirEntidadLectura(Map<String, Object> lecturaItem,
                                            User user,
                                            Long idCliente,
                                            List<MultimediaLecturas> fotos) throws ParseException {
        Cliente cliente = iCliente.findById(idCliente).orElse(null);
        // Obtener el ID externo de la lectura que viene del front


        // Buscar si ya existe una lectura con ese ID externo
        Optional<Lectura> lecturaExistente = Optional.ofNullable(ilecturas.findByCliente(cliente));

        // Si existe, actualizamos la lectura existente
        if (lecturaExistente.isPresent()) {
            Lectura lectura = lecturaExistente.get();

            // Actualizar campos modificables
            lectura.setLectura(getIntValue(lecturaItem.get("lectura")));
            lectura.setCausal(getCausal(lecturaItem));
            lectura.setObservacion1(getObservacion(lecturaItem, "fk_obs1"));
            lectura.setObservacion2(getObservacion(lecturaItem, "fk_obs2"));
            lectura.setObservacion3(getObservacion(lecturaItem, "fk_obs3"));
            lectura.setParametros(getParametros(lecturaItem));
            lectura.setFecha(parseFecha((String) lecturaItem.get("fecha")));
            lectura.setFotos(fotos);
            lectura.setUser(user);

            return lectura;
        }
        // Si no existe, creamos una nueva lectura
        else {
            if (cliente != null) {
                cliente.setLecturaCompletada(true);
                iCliente.save(cliente);
            }
            return Lectura.builder()
                    .user(user)
                    .cliente(cliente)
                    .lectura(getIntValue(lecturaItem.get("lectura")))
                    .causal(getCausal(lecturaItem))
                    .observacion1(getObservacion(lecturaItem, "fk_obs1"))
                    .observacion2(getObservacion(lecturaItem, "fk_obs2"))
                    .observacion3(getObservacion(lecturaItem, "fk_obs3"))
                    .parametros(getParametros(lecturaItem))
                    .fecha(parseFecha((String) lecturaItem.get("fecha")))
                    .fotos(fotos)
                    .build();
        }
    }

}
