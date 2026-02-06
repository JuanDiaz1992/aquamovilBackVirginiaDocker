package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.lecturas;

import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IObservaciones;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IParametros;
import com.springboot.aldiabackjava.repositories.rutasLecturaCritica.IRutaLecturaCritica;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BasicTablesLecturasServices {
    private final IParametros iParametros;
    private final ICausal iCausal;
    private final IObservaciones iObservaciones;
    private final IRuta iRuta;
    private final IRutaLecturaCritica iRutaLecturaCritica;

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAllParametros(){
        Map<String, Object> response = new HashMap<>();
        try {
            List<Parametros> parametrosList = iParametros.findAll();
            if (parametrosList.isEmpty()) {
                response.put("status", 404);
                response.put("message", "La tabla de parámetros está vacía");
                return ResponseEntity.ok(response);
            }
            iParametros.deleteAll();
            response.put("status", 200);
            response.put("message", "Todos los parámetros  han sido eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("error", "No se pueden eliminar los parámetros porque existen registros relacionados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error inesperado al eliminar los parámetros.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAllCausales() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Causal> causalList = iCausal.findAll();
            if (causalList.isEmpty()) {
                response.put("status", 404);
                response.put("message", "La tabla de causales está vacía");
                return ResponseEntity.ok(response);
            }
            iCausal.deleteAll();
            response.put("status", 200);
            response.put("message", "Todos los causales  han sido eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("error", "No se pueden eliminar los causales porque existen registros relacionados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error inesperado al eliminar los causales.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAllObservaciones() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Observaciones> observacionesList = iObservaciones.findAll();
            if (observacionesList.isEmpty()) {
                response.put("status", 404);
                response.put("message", "La tabla de observaciones está vacía");
                return ResponseEntity.ok(response);
            }
            iObservaciones.deleteAll();
            response.put("status", 200);
            response.put("message", "Todos las observaciones  han sido eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("error", "No se pueden eliminar las observaciones porque existen registros relacionados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error inesperado al eliminar las observaciones.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteAllRutas() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ruta> rutaList = iRuta.findAll();
            List<RutaLecturaCritica> rutaLecturaCriticasList = iRutaLecturaCritica.findAll();
            if (rutaList.isEmpty() && rutaLecturaCriticasList.isEmpty()) {
                response.put("status", 404);
                response.put("message", "La tabla de rutas está vacía");
                return ResponseEntity.ok(response);
            }
            iRuta.deleteAll();
            iRutaLecturaCritica.deleteAll();
            response.put("status", 200);
            response.put("message", "Todos las rutas han sido eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            response.put("error", "No se pueden eliminar las rutas porque existen registros relacionados.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "Ocurrió un error inesperado al eliminar las rutas.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
