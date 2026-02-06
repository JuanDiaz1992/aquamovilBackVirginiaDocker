package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.critica;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturasCriticas.CriticaLectura;
import com.springboot.aldiabackjava.models.lecturasCriticas.SolicitudLecturaCritica;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Crc;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ICriticaLectura;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.IMultimediaCriticas;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.ISolicitudLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriticasDesktopServices {
    private final ICliente iCliente;
    private final ISolicitudLecturaCritica iSolicitudLecturaCritica;
    private final ICriticaLectura iCriticaLectura;
    private final IRutaLecturaCritica iRutaLecturaCritica;
    private final Conversores conversores;
    private final IMultimediaCriticas iMultimediaCriticas;

    public ResponseEntity<Map<String, Object>> setSolicitudCritica(Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try{
            Cliente cliente = iCliente.findById(Long.parseLong(data.get("idCliente").toString())).orElse(null);
            List<SolicitudLecturaCritica> listSolicitudLecturaCriticasCliente = iSolicitudLecturaCritica.findByCliente(cliente);
            boolean yaTieneSolicitudEsteMes = listSolicitudLecturaCriticasCliente.stream().anyMatch(solicitud -> {
                Calendar fechaSolicitud = Calendar.getInstance();
                fechaSolicitud.setTime(solicitud.getFechaCreacion());

                Calendar ahora = Calendar.getInstance();

                return fechaSolicitud.get(Calendar.YEAR) == ahora.get(Calendar.YEAR)
                        && fechaSolicitud.get(Calendar.MONTH) == ahora.get(Calendar.MONTH);
            });
            if (yaTieneSolicitudEsteMes){
                response.put("message", "El usuario ya cuenta con una solicitud este mes");
                response.put("status",500);
                return ResponseEntity.badRequest().body(response);
            }
            if (cliente != null){
                SolicitudLecturaCritica solicitudLecturaCritica = SolicitudLecturaCritica.builder()
                        .cliente(cliente)
                        .motivo("SOLICITUD FINANCIERO")
                        .build();
                iSolicitudLecturaCritica.save(solicitudLecturaCritica);
                response.put("message", "Solicitud realizada correctamente");
                response.put("status",200);
                return ResponseEntity.ok().body(response);
            }
            response.put("message", "Error al realizar la solicitud, verifique que el usuario exista en el sistema");
            response.put("status",500);
            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            log.info(e.toString());
            response.put("message", "Error al realizar la solicitud, valide con el administrador");
            response.put("status",500);
            return ResponseEntity.badRequest().body(response);
        }

    }

    public ResponseEntity<Map<String, Object>> getSolicitudesCritica() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> solicitudes = iSolicitudLecturaCritica.getSolicitudesCriticaInfo();

            if (!solicitudes.isEmpty()) {
                response.put("total", solicitudes.size());
                response.put("solicitudes", solicitudes);
                response.put("status", 200);
                return ResponseEntity.ok(response);
            }

            response.put("message", "No existen solicitudes");
            response.put("status", 200);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al consultar solicitudes críticas", e);
            response.put("message", "Error al realizar la consulta");
            response.put("status", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> deleteSolicitud(Long idSolicitud) {
        Map<String, Object> response = new HashMap<>();
        log.info(idSolicitud.toString());

        try {
            SolicitudLecturaCritica solicitudLecturaCritica = iSolicitudLecturaCritica.findById(idSolicitud).orElse(null);

            if (solicitudLecturaCritica == null) {
                response.put("message", "Solicitud no encontrada");
                response.put("status", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Eliminar crítica de lectura si está completada
            if (solicitudLecturaCritica.isCompletada()) {
                CriticaLectura criticaLectura = iCriticaLectura.findByCliente(solicitudLecturaCritica.getCliente());
                if (criticaLectura != null) {
                    iCriticaLectura.delete(criticaLectura);
                    Cliente cliente = criticaLectura.getCliente();
                    cliente.setCriticaLecturaCompletada(false);
                    iCliente.save(cliente);
                }
            }

            // Obtener la ruta antes de eliminar la solicitud
            Cliente cliente = solicitudLecturaCritica.getCliente();
            RutaLecturaCritica ruta = cliente.getRutaLecturaCritica();

// Eliminar la solicitud
            iSolicitudLecturaCritica.delete(solicitudLecturaCritica);

// Verificar si ya no quedan más solicitudes en esa ruta
            List<SolicitudLecturaCritica> solicitudesRestantes = iSolicitudLecturaCritica.findByCliente_RutaLecturaCritica(ruta);
            if (solicitudesRestantes.isEmpty()) {
                ruta.setUser(null); // Desasociar el usuario de la ruta
                iRutaLecturaCritica.save(ruta); // Guardar la ruta sin usuario
                response.put("info", "Era la última solicitud, usuario desasignado de la ruta");
            }

            response.put("message", "Solicitud eliminada");
            response.put("status", 200);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("Error al eliminar solicitud: ", e);
            response.put("message", "Error al eliminar la solicitud");
            response.put("status", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<byte[]> exportSolicitudesCSV(){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024); // Buffer inicial de 1MB

            // BOM para UTF-8
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);

            try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                // Escribir encabezados
                writer.write("idCritica,IdCliente,PersonaQueAtiende,Documento,Teléfono,Fecha,Lectura,Motivo,Observaciones,Respuestas,IdUsuario,IdSolicitud\n");

                // Procesar todos los clientes (versión simplificada sin paginación)
                List<CriticaLectura> criticaLecturas = iCriticaLectura.findAll();

                // Procesar cada cliente
                for (CriticaLectura critica : criticaLecturas) {

                    // Construir línea CSV
                    writer.write(
                            conversores.safe(critica.getIdCriticaLectura()) + "," +
                            conversores.safe(critica.getCliente().getIdCliente()) + "," +
                            conversores.safe(critica.getAtendio() != null? critica.getAtendio() : null) + "," +
                            conversores.safe(critica.getDocumentoAntendio() != null ? critica.getDocumentoAntendio() : null) + "," +
                            conversores.safe(critica.getTelefono() != null ? critica.getTelefono()  : null) + "," +
                            conversores.safe(critica.getFecha() !=null ? critica.getFecha() : null) + "," +
                            conversores.safe(critica.getLectura() != null ? critica.getLectura()  : null) + "," +
                            conversores.safe(critica.getMotivo() != null ? critica.getMotivo()  : null) + "," +
                            conversores.safe(critica.getObservaciones() != null ? critica.getObservaciones()  : null) + "," +
                            conversores.safe(critica.getRespuestas() != null ? critica.getRespuestas()  : null) + "," +
                            conversores.safe(critica.getUser().getIdUser()) + "," +
                            conversores.safe(critica.getSolicitudLecturaCritica().getIdSolicitudCriticaLectura()) + "\n"

                    );
                }

                writer.flush();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=criticas.csv")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(out.toByteArray());

        } catch (Exception e) {
            log.error("Error en exportación CSV", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> eliminarCriticaLecturas() {
        Map<String, Object> response = new HashMap<>();
        try{
            List<Cliente> clienteList = iCliente.findAll().stream()
                    .peek(cliente -> {
                        cliente.setCriticaLecturaCompletada(false);
                    })
                    .collect(Collectors.toList());
            iCliente.saveAll(clienteList);
            iSolicitudLecturaCritica.deleteAllSolicitudLecturaCritica();
            iMultimediaCriticas.deleteAllMultimediaCriticas();
            iCriticaLectura.deleteAllCriticaLectura();
            List<RutaLecturaCritica> rutaList = iRutaLecturaCritica.findAll().stream()
                    .peek(ruta->{
                        ruta.setUser(null);
                    }).collect(Collectors.toList());
            iRutaLecturaCritica.saveAll(rutaList);
            response.put("status",200);
            response.put("message", "Eliminación completada");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("status",400);
            response.put("message", "Error al realizar la eliminación");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
