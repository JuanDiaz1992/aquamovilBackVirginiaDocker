package com.springboot.aldiabackjava.services.AquaMovilServices.solicitudes;

import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
import com.springboot.aldiabackjava.models.solicitudes.TipoSolicitud;
import com.springboot.aldiabackjava.repositories.solicitudes.ITipoSolicitud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TipoSolicitudServices {
    private final ITipoSolicitud iTipoSolicitud;

    public ResponseEntity<Map<String, Object>> deleteAllTipoSolicitudes() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TipoSolicitud> tipoSolicitudList = iTipoSolicitud.findAll();
            if (tipoSolicitudList.isEmpty()) {
                response.put("status", 404);
                response.put("message", "La tabla de tipos de solicitudes está vacía");
                return ResponseEntity.ok(response);
            }
            iTipoSolicitud.deleteAll();
            response.put("status", 200);
            response.put("message", "Todos los tipos de solicitudes han sido eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("error", "No se pueden eliminar los tipos de solicitudes porque existen registros relacionados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error inesperado al eliminar los tipos de servicios.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getTiposSolicitudesServices() {
        Map<String, Object> response = new HashMap<>();
        try{
            List<TipoSolicitud> tipoSolicitudList = iTipoSolicitud.findAll();
            response.put("tiposSolicitud", tipoSolicitudList);
            response.put("status", 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error leyendo archivo", e);
            response.put("message", "Ah ocurrido un error consultando los tipos de solicitudes" + e.getMessage());
            response.put("status", 500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
