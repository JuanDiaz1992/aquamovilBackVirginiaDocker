package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.preSolicitudes;


import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.SubCotizaciones;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.Solicitud;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.solicitudes.IProductosYServicios;
import com.springboot.aldiabackjava.repositories.solicitudes.ITipoSolicitud;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.IAutorizacionTrabajo;
import com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo.ISubCotizaciones;
import com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes.ISolicitud;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudesDesktopServices {
    private final ISolicitud iSolicitud;
    private final ITipoSolicitud iTipoSolicitud;
    private final IUserRepository iUserRepository;
    private final ISubCotizaciones iSubCotizaciones;
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final IProductosYServicios iProductosYServicios;
    private final Conversores conversores;
    public ResponseEntity<Map<String, Object>> getSolicitudesDesktopServices() {
        Map response = new HashMap();
        try{
            List<Map<String, Object>> solicitudList = iSolicitud.findAll().stream()
                    .map(solicitudTrabajo ->{
                        AutorizacionTrabajo autorizacionTrabajo = iAutorizacionTrabajo.findByIdOt(solicitudTrabajo.getIdOt());
                        List<Object[]> subcotizacionesRaw = iSubCotizaciones.findReducidasByAutorizacion(autorizacionTrabajo);
                        List<Map<String, Object>> subCotizaciones = new ArrayList<>();
                        subcotizacionesRaw.forEach(row -> {
                            ProductosYServicios productosYServicios = iProductosYServicios.findById((Long) row[1]).orElse(null);
                            Map<String, Object> map = new HashMap<>();
                            map.put("idOt", row[0]);
                            map.put("idItem", row[1]);
                            map.put("nombre", productosYServicios.getNombreItem());
                            map.put("codigo", productosYServicios.getCodigo());
                            map.put("idCotizacion", row[2]);
                            map.put("cantidad", row[3]);
                            map.put("vrUnitario", row[4]);
                            map.put("valor", row[5]);
                            map.put("valorAnterior", row[6]);
                            map.put("descripcion", row[7]);
                            map.put("idCargo", row[8]);
                            map.put("idServicio", row[9]);
                            map.put("asume", row[10]);
                            map.put("documentoOperario", row[11]);
                            subCotizaciones.add(map);
                        });
                        Map<String, Object> solicitud = new HashMap<>();
                        solicitud.put("idOt", solicitudTrabajo.getIdOt());
                        solicitud.put("idSolicitud", solicitudTrabajo.getIdSolicitud());
                        solicitud.put("tipoSolcitud", iTipoSolicitud.findById(Long.valueOf(solicitudTrabajo.getTipoSolicitud())));
                        solicitud.put("tipoSolicitud2", iTipoSolicitud.findById(Long.valueOf(solicitudTrabajo.getIdTipoSolicitud2())));
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
                        solicitud.put("subCotizaciones", subCotizaciones);
                        solicitud.put("user", solicitudTrabajo.getUser());
                        if (autorizacionTrabajo != null) {
                            solicitud.put("vrOt", autorizacionTrabajo.getVrOt());
                        }
                        return solicitud;
                    })
                    .toList();
            List<User> userList = iUserRepository.findAll();
            response.put("solicitudes", solicitudList);
            response.put("users", userList);
            response.put("total", solicitudList.size());
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Ah ocurrido un error, "+e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<byte[]> exportSolicitudesYSubCotizacionesCSV() {
        try {
            // Crear un stream en memoria para el ZIP
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);

            // 1. Generar archivo de solicitudes
            ByteArrayOutputStream solicitudesOut = new ByteArrayOutputStream();
            solicitudesOut.write(0xEF);
            solicitudesOut.write(0xBB);
            solicitudesOut.write(0xBF);

            try (OutputStreamWriter writer = new OutputStreamWriter(solicitudesOut, StandardCharsets.UTF_8)) {
                // Escribir encabezados
                writer.write(
                        "idOt,idSolicitud,tipoSolicitud,idTipoSolicitud2,idCliente," +
                                "idPerido,idPresentacion,fecha,notas,nombreSolicitud," +
                                "telefono,cedula,correo,completado,idUser," +
                                "documentoDeQuienAtiende,notasOperario,sincronizado,firma\n"
                );

                // Escribir datos de solicitudes
                List<Solicitud> solicitudList = iSolicitud.findAll();
                for (Solicitud solicitud : solicitudList) {
                    writer.write(
                            conversores.safe(solicitud.getIdOt()) + "," +
                                    conversores.safe(solicitud.getIdSolicitud()) + "," +
                                    conversores.safe(solicitud.getTipoSolicitud()) + "," +
                                    conversores.safe(solicitud.getIdTipoSolicitud2()) + "," +
                                    conversores.safe(solicitud.getCliente() != null ? solicitud.getCliente().getIdCliente() : "") + "," +
                                    conversores.safe(solicitud.getIdPerido()) + "," +
                                    conversores.safe(solicitud.getIdPresentacion()) + "," +
                                    conversores.safe(solicitud.getFecha()) + "," +
                                    conversores.safe(solicitud.getNotas()) + "," +
                                    conversores.safe(solicitud.getNombreSolicitud()) + "," +
                                    conversores.safe(solicitud.getTelefono()) + "," +
                                    conversores.safe(solicitud.getCedula()) + "," +
                                    conversores.safe(solicitud.getCorreo()) + "," +
                                    conversores.safe(solicitud.getCompletado()) + "," +
                                    conversores.safe(solicitud.getUser() != null ? solicitud.getUser().getIdUser() : "") + "," +
                                    conversores.safe(solicitud.getDocumentoDeQuienAtiende()) + "," +
                                    conversores.safe(solicitud.getNotasOperario()) + "," +
                                    conversores.safe(solicitud.getSincronizado()) + "," +
                                    conversores.safe(solicitud.getFirma()) + "\n"
                    );
                }
                writer.flush();
            }

            // Añadir solicitudes.csv al ZIP
            zipOut.putNextEntry(new ZipEntry("solicitudes.csv"));
            zipOut.write(solicitudesOut.toByteArray());
            zipOut.closeEntry();

            // 2. Generar archivo de subcotizaciones
            ByteArrayOutputStream subCotizacionesOut = new ByteArrayOutputStream();
            subCotizacionesOut.write(0xEF);
            subCotizacionesOut.write(0xBB);
            subCotizacionesOut.write(0xBF);

            try (OutputStreamWriter writer = new OutputStreamWriter(subCotizacionesOut, StandardCharsets.UTF_8)) {
                // Escribir encabezados
                writer.write(
                        "idCotizacion,idOt,idProducto,nombreProducto,codigoProducto,cantidad," +
                                "valorUnitario,valorTotal,valorAnterior,descripcion,idCargo," +
                                "idServicio,asume,documentoOperario\n"
                );

                // Escribir datos de subcotizaciones
                List<SubCotizaciones> subCotizacionesList = iSubCotizaciones.findAll();
                for (SubCotizaciones sc : subCotizacionesList) {
                    ProductosYServicios producto = sc.getProductosYServicios();
                    writer.write(
                            conversores.safe(sc.getIdCotizacion()) + "," +
                                    conversores.safe(sc.getAutorizacionTrabajo() != null ? sc.getAutorizacionTrabajo().getIdOt() : "") + "," +
                                    conversores.safe(producto != null ? producto.getIdItem() : "") + "," +
                                    conversores.safe(producto != null ? producto.getNombreItem() : "") + "," +
                                    conversores.safe(producto != null ? producto.getCodigo() : "") + "," +
                                    conversores.safe(sc.getCantidad()) + "," +
                                    conversores.safe(sc.getVrUnitario()) + "," +
                                    conversores.safe(sc.getValor()) + "," +
                                    conversores.safe(sc.getValorAnterior()) + "," +
                                    conversores.safe(sc.getDescripcion()) + "," +
                                    conversores.safe(sc.getIdCargo()) + "," +
                                    conversores.safe(sc.getIdServicio()) + "," +
                                    conversores.safe(sc.getAsume()) + "," +
                                    conversores.safe(sc.getDocumentoOperario()) + "\n"
                    );
                }
                writer.flush();
            }

            // Añadir subcotizaciones.csv al ZIP
            zipOut.putNextEntry(new ZipEntry("subcotizaciones.csv"));
            zipOut.write(subCotizacionesOut.toByteArray());
            zipOut.closeEntry();

            // Finalizar el ZIP
            zipOut.finish();
            zipOut.close();

            // Configurar respuesta HTTP
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reportes.zip")
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(baos.toByteArray());

        } catch (Exception e) {
            log.error("Error en exportación combinada", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<Map<String, Object>> asignarSolicitudTrabajo(Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> asignaciones = (List<Map<String, Object>>) data.get("asignaciones");
            List<String> resultados = new ArrayList<>();

            for (Map<String, Object> asignacion : asignaciones) {
                Long idOt = Long.valueOf(asignacion.get("idOt").toString());
                Long idUser = Long.valueOf(asignacion.get("idUser").toString());

                User user = iUserRepository.findById(idUser).orElse(null);
                Solicitud solicitud = iSolicitud.findById(idOt).orElse(null);

                if (user != null && solicitud != null) {
                    solicitud.setUser(user);
                    iSolicitud.save(solicitud);
                    resultados.add("Solicitud " + idOt + " asignada a " + user.getName());
                } else {
                    resultados.add("Error: No se encontró solicitud o usuario para OT " + idOt);
                }
            }

            response.put("message", resultados);
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Ha ocurrido un error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
