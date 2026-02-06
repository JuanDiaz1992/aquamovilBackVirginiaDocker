package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.SubCotizaciones;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.Solicitud;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutorizacionesDektopServices {
    private final IAutorizacionTrabajo iAutorizacionTrabajo;
    private final IUserRepository iUserRepository;
    private final ISubCotizaciones iSubCotizaciones;
    private final IProductosYServicios iProductosYServicios;
    private final ITipoSolicitud iTipoSolicitud;
    private final ISolicitud iSolicitud;
    private final Conversores conversores;
    private final ICliente iCliente;

    public ResponseEntity<Map<String, Object>> getAutorizacionesTrabajoServices() {

        Map response = new HashMap();
        try{
            List<Map<String, Object>> autorizaciones = iAutorizacionTrabajo.findAll().stream()
                    .map(autorizacionTrabajo ->{
                        List<Object[]> subcotizacionesRaw = iSubCotizaciones.findReducidasByAutorizacion(autorizacionTrabajo);
                        Solicitud solicitud = iSolicitud.findById(autorizacionTrabajo.getIdOt()).orElse(null);
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
                        Map<String, Object> autorizacion = new HashMap<>();
                        autorizacion.put("idOt", autorizacionTrabajo.getIdOt());
                        autorizacion.put("idSolicitud", autorizacionTrabajo.getIdSolicitud());
                        autorizacion.put("tipoSolcitud", iTipoSolicitud.findById(Long.valueOf(solicitud.getTipoSolicitud())));
                        autorizacion.put("tipoSolicitud2", iTipoSolicitud.findById(Long.valueOf(solicitud.getIdTipoSolicitud2())));
                        autorizacion.put("idCliente", autorizacionTrabajo.getCliente().getIdCliente());
                        autorizacion.put("notas", solicitud.getNotas());
                        autorizacion.put("fecha", autorizacionTrabajo.getFecha());
                        autorizacion.put("nombreSolicitud", solicitud.getNombreSolicitud());
                        autorizacion.put("telefono", solicitud.getTelefono());
                        autorizacion.put("cedula", solicitud.getCedula());
                        autorizacion.put("correo", solicitud.getCorreo());
                        autorizacion.put("status" , autorizacionTrabajo.getStatus());
                        autorizacion.put("firma" ,autorizacionTrabajo.getFirma());
                        autorizacion.put("notasOperario", solicitud.getNotasOperario());
                        autorizacion.put("notasOperarioFinal" , autorizacionTrabajo.getNotasOperario());
                        autorizacion.put("documentoDeQuienAtiende" , autorizacionTrabajo.getDocumentoDeQuienAtiende());
                        autorizacion.put("subCotizaciones", subCotizaciones);
                        autorizacion.put("fechaRealizacion", autorizacionTrabajo.getFechaRealizacion());
                        autorizacion.put("user", autorizacionTrabajo.getUser());
                        autorizacion.put("vrOt", autorizacionTrabajo.getVrOt());
                        autorizacion.put("vrCotizacion", autorizacionTrabajo.getVrCotizacion());
                        return autorizacion;
                    })
                    .toList();
            List<User> userList = iUserRepository.findAll();
            response.put("autorizaciones", autorizaciones);
            response.put("users", userList);
            response.put("total", autorizaciones.size());
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "Ah ocurrido un error, "+e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

    public ResponseEntity<Map<String, Object>> asignarAutorizacionTrabajo(Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> asignaciones = (List<Map<String, Object>>) data.get("asignaciones");
            List<String> resultados = new ArrayList<>();

            for (Map<String, Object> asignacion : asignaciones) {
                Long idOt = Long.valueOf(asignacion.get("idOt").toString());
                Long idUser = Long.valueOf(asignacion.get("idUser").toString());

                User user = iUserRepository.findById(idUser).orElse(null);
                AutorizacionTrabajo autorizacionTrabajo = iAutorizacionTrabajo.findById(idOt).orElse(null);

                if (user != null && autorizacionTrabajo != null) {
                    autorizacionTrabajo.setUser(user);
                    iAutorizacionTrabajo.save(autorizacionTrabajo);
                    resultados.add("Autorización " + idOt + " asignada a " + user.getName());
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

    public ResponseEntity<byte[]> exportAutorizacionesYSubCotizacionesCSV() {
        try {
            // Crear un stream en memoria para el ZIP
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);

            // 1. Generar archivo de solicitudes
            ByteArrayOutputStream autorizacionesOut = new ByteArrayOutputStream();
            autorizacionesOut.write(0xEF);
            autorizacionesOut.write(0xBB);
            autorizacionesOut.write(0xBF);

            try (OutputStreamWriter writer = new OutputStreamWriter(autorizacionesOut, StandardCharsets.UTF_8)) {
                // Escribir encabezados
                writer.write(
                        "idOt,idSolicitud,idTipoSolicitud,idTipoSolicitud2,idCliente," +
                                "fecha,fechaRealizacion,observacion,notasSoicitud,notasOperario,vrCotizacion,vrOt," +
                                "telefono,cedula,correo,completado,idUser," +
                                "documentoDeQuienAtiende,firma\n"
                );

                // Escribir datos de solicitudes
                List<AutorizacionTrabajo> autorizacionTrabajoList = iAutorizacionTrabajo.findAll();
                for (AutorizacionTrabajo autorizacionTrabajo : autorizacionTrabajoList) {
                    Solicitud solicitud = iSolicitud.findById(autorizacionTrabajo.getIdOt()).orElse(null);

                    writer.write(
                            conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getIdOt() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getIdSolicitud() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getTipoSolicitud() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getIdTipoSolicitud2() : null) + "," +
                                    conversores.safe(solicitud != null && solicitud.getCliente() != null ? solicitud.getCliente().getIdCliente() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getFecha() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getFechaRealizacion() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getNotas() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getNotasOperario() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getNotasOperario() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getVrCotizacion() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getVrOt() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getTelefono() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getCedula() : null) + "," +
                                    conversores.safe(solicitud != null ? solicitud.getCorreo() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getStatus() : null) + "," +
                                    conversores.safe(solicitud != null && solicitud.getUser() != null ? solicitud.getUser().getIdUser() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null && autorizacionTrabajo.getDocumentoDeQuienAtiende() != null ? autorizacionTrabajo.getDocumentoDeQuienAtiende().toString() : null) + "," +
                                    conversores.safe(autorizacionTrabajo != null ? autorizacionTrabajo.getFirma() : null) + "\n"
                    );
                }

                writer.flush();
            }

            // Añadir solicitudes.csv al ZIP
            zipOut.putNextEntry(new ZipEntry("autorizaciones.csv"));
            zipOut.write(autorizacionesOut.toByteArray());
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
            return ResponseEntity.internalServerError().build();
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAutorizacion(Long id) {
        Map<String, Object> response = new HashMap<>();
        try{
            AutorizacionTrabajo autorizacionTrabajo = iAutorizacionTrabajo.findByIdOt(id);
            if(autorizacionTrabajo != null){
                List<SubCotizaciones> subCotizacionesList = iSubCotizaciones.findByAutorizacionTrabajo(autorizacionTrabajo);
                if(subCotizacionesList != null){
                    iSubCotizaciones.deleteAll(subCotizacionesList);
                }

            }
            Solicitud solicitud = iSolicitud.findById(autorizacionTrabajo.getIdOt()).orElse(null);
            solicitud.setSincronizado(false);
            solicitud.setCompletado(false);
            solicitud.setFirma("");
            solicitud.setDocumentoDeQuienAtiende("");
            solicitud.setNotasOperario("");
            Cliente cliente = solicitud.getCliente();
            cliente.setAutorizacionTrabajoCompletada(false);
            cliente.setPreSolicitudCompletada(false);
            iCliente.save(cliente);
            iSolicitud.save(solicitud);
            iAutorizacionTrabajo.delete(autorizacionTrabajo);
            response.put("message","Autorización eliminada");
            response.put("status", 200);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
