package com.springboot.aldiabackjava.services.AquaMovilServices;

import com.springboot.aldiabackjava.models.Ciclo;
import com.springboot.aldiabackjava.models.lecturasCriticas.PreguntaEncuesta;
import com.springboot.aldiabackjava.models.lecturasCriticas.RespuestasEncuestas;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.ICausal;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IObservaciones;
import com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones.IParametros;
import com.springboot.aldiabackjava.repositories.ICiclo;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.IPreguntaEncuesta;
import com.springboot.aldiabackjava.repositories.lecturasCriticas.IRespuestasEncuestas;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AditionalServices {
    private final ICausal iCausal;
    private final IObservaciones iObservaciones;
    private final IParametros iParametros;
    private final IRuta iRuta;
    private final ICiclo iCiclo;
    private final IPreguntaEncuesta iPreguntaEncuesta;
    private final IRespuestasEncuestas iRespuestasEncuestas;

    public ResponseEntity<Map<String, Object>> getOterTables() {
        Map<String, Object> response = new HashMap<>();
        try{
            //Obtener Causales
            List<Causal> causalList = new ArrayList<>();
            iCausal.findAll().forEach(causalList::add);

            //Obtener Observaciones
            List<Observaciones> observacionesList = new ArrayList<>();
            iObservaciones.findAll().forEach(observacionesList::add);

            //Obtener todas los Ciclos
            List<Ciclo> cicloList = new ArrayList<>();
            iCiclo.findAll().forEach(cicloList::add);

            //Obtener todas las rutas
            List<Ruta> rutaList = new ArrayList<>();
            iRuta.findAll().forEach(rutaList::add);

            //Obtener Parametros
            List<Parametros> parametrosList = new ArrayList<>();
            iParametros.findAll().forEach(parametrosList::add);

            response.put("causales", causalList);
            response.put("observaciones", observacionesList);
            response.put("parametros", parametrosList);
            response.put("ciclos", cicloList);
            response.put("rutas",rutaList);
            response.put("status", 200);

        }catch (Exception e){
            log.error("Error al obtener las tablas: {}", e.getMessage(), e);
            response.put("status", 500);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Map<String, Object>> getPreguntasCritica (){
        Map<String, Object> response = new HashMap<>();
        try{
            //Obtener Causales
            List<PreguntaEncuesta> preguntaEncuestas = iPreguntaEncuesta.findAll();
            List<Map<String, Object>> respuestasEncuestas = iRespuestasEncuestas.findAll().stream()
                    .map(respuesta -> {
                        Map<String, Object> respuestaTemp = new HashMap<>();
                        respuestaTemp.put("idRespuesta", respuesta.getIdRespuesta());
                        respuestaTemp.put("textoRespuesta", respuesta.getRespuesta());
                        respuestaTemp.put("idPregunta", respuesta.getPreguntaEncuesta().getIdPregunta());
                        return respuestaTemp;
                    })
                    .toList();
            response.put("preguntaEncuestas", preguntaEncuestas);
            response.put("respuestasEncuestas", respuestasEncuestas);
            response.put("status", 200);

        }catch (Exception e){
            log.error("Error al obtener las tablas: {}", e.getMessage(), e);
            response.put("status", 500);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }
}
