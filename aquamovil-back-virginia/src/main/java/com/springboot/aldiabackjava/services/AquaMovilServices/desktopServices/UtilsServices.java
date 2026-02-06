package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices;

import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.TipoSolicitud;
import com.springboot.aldiabackjava.repositories.IHistorialLecturas;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IObservaciones;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IParametros;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturas.ILecturas;
import com.springboot.aldiabackjava.repositories.lecturas.IMultimediaLecturas;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ICriticaLectura;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.IMultimediaCriticas;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturas.ICrc;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import com.springboot.aldiabackjava.repositories.solicitudes.IProductosYServicios;
import com.springboot.aldiabackjava.repositories.solicitudes.ITipoSolicitud;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.IAutorizacionTrabajo;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.IMultimediaAutorizaciones;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.ISubCotizaciones;
import com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes.IMultimediaPresolicitudes;
import com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes.ISolicitud;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class UtilsServices {
    private final ICliente iCliente;
    private final ILecturas iLecturas;
    private final IRuta iRuta;
    private final IRutaLecturaCritica iRutaLecturaCritica;
    private final ICriticaLectura iCriticaLectura;
    private final ISolicitudLecturaCritica iSolicitudLecturaCritica;
    private final IHistorialLecturas iHistorialLecturas;
    private final ISolicitud iSolicitud;
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final IMultimediaPresolicitudes iMultimediaPresolicitudes;
    private final IMultimediaAutorizaciones iMultimediaAutorizaciones;
    private final IMultimediaLecturas iMultimediaLecturas;
    private final IMultimediaCriticas iMultimediaCriticas;
    private final ISubCotizaciones iSubCotizaciones;
    private final ICrc iCrc;
    private final IProductosYServicios iProductosYServicios;
    private final ITipoSolicitud iTipoSolicitud;
    private final ICausal iCausal;
    private final IObservaciones iObservaciones;
    private final IParametros iParametros;
    @Value("${user.photos.base.path}")
    private String USER_PHOTOS_BASE_PATH;
    @Value("${logging.file.path:./logs}")
    private String logsPath;

    public ResponseEntity<Resource> exportLog(String date) {
        String filename = "aquamovil." + date + ".log";
        Path logFilePath = Paths.get(logsPath, filename);

        if (!Files.exists(logFilePath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(logFilePath.toFile()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log-" + date + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(logFilePath))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAllTables() {
        Map<String, Object> response = new HashMap<>();
        try {
            //Eliminar todas las tablas de multimedia
            iMultimediaLecturas.deleteAllMultimediaLecturas();
            iMultimediaCriticas.deleteAllMultimediaCriticas();
            iMultimediaPresolicitudes.deleteAllMultimediaPresolicitudes();
            iMultimediaAutorizaciones.deleteAllMultimediaAutorizaciones();

            //Eliminar resto de tablas principales
            iLecturas.deleteAllLecturas();
            iCriticaLectura.deleteAllCriticaLectura();
            iSolicitudLecturaCritica.deleteAllSolicitudLecturaCritica();
            iHistorialLecturas.deleteAllHistorialLecturas();
            iSubCotizaciones.deleteAllSubCotizaciones();
            iAutorizacionTrabajo.deleteAllAutorizacionTrabajo();
            iSolicitud.deleteAllSolicitudes();
            iCrc.deleteAllCrc();
            iCliente.deleteAllCliente();

            List<Ruta> rutaList = iRuta.findAll().stream()
                    .peek(ruta->{
                        ruta.setEstado(false);
                        ruta.setUser(null);
                    }).collect(Collectors.toList());
            iRuta.saveAll(rutaList);
            List<RutaLecturaCritica> rutaLecturaCriticaList = iRutaLecturaCritica.findAll().stream()
                    .peek(ruta->{
                        ruta.setUser(null);
                    }).collect(Collectors.toList());
            iRuta.saveAll(rutaList);
            iRutaLecturaCritica.saveAll(rutaLecturaCriticaList);

            Path photosDir = Paths.get(USER_PHOTOS_BASE_PATH);
            if (Files.exists(photosDir)) {
                try (Stream<Path> paths = Files.walk(photosDir)) {
                    paths.sorted(Comparator.reverseOrder())
                            .filter(path -> !path.equals(photosDir))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    throw new RuntimeException("Error al borrar archivo: " + path, e);
                                }
                            });
                }
            }

            response.put("status", 200);
            response.put("message", "Se ha limpiado la base de datos y la carpeta multimedia correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al limpiar la base de datos");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getAllBasicaltables() {
        Map<String, Object> response = new HashMap<>();
        try{
            List<ProductosYServicios> productosYServiciosList = iProductosYServicios.findAll();
            List<TipoSolicitud> tipoSolicitudList = iTipoSolicitud.findAll();
            List<Causal> causalList = iCausal.findAll();
            List<Observaciones> observacionesList = iObservaciones.findAll();
            List<Parametros> parametrosList = iParametros.findAll();
            List<Ruta> rutaList = iRuta.findAll();
            response.put("status", 200);
            response.put("productosYServiciosList", productosYServiciosList);
            response.put("tipoSolicitudList", tipoSolicitudList);
            response.put("causalList", causalList);
            response.put("observacionesList", observacionesList);
            response.put("parametrosList", parametrosList);
            response.put("rutaList",rutaList);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            response.put("status", 400);
            response.put("message", "Error al obtener tablas" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}