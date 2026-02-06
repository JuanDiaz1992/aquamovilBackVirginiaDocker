package com.springboot.aldiabackjava.services.AquaMovilServices.lecturas;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.Ciclo;
import com.springboot.aldiabackjava.models.rutasLecturas.Crc;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.cliente.ICliente;
import com.springboot.aldiabackjava.repositories.ICiclo;
import com.springboot.aldiabackjava.repositories.rutasLecturas.ICrc;
import com.springboot.aldiabackjava.repositories.rutasLecturas.IRuta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrcServices {
    private final JwtInterceptor jwtInterceptor;
    private final ICliente iCliente;
    private final IRuta iRuta;
    private final ICrc iCrc;
    private final ICiclo iCiclo;
    public ResponseEntity<Map<String, Object>> setCrc(Map<String, Object> crcs) {
        User user = jwtInterceptor.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        log.info(crcs.toString());
        try {
            List<Map<String, Object>> ListCrcs = (List<Map<String, Object>>) crcs.get("crcs");
            List<Crc> crcsForSave = new ArrayList<>();
            if (!crcs.isEmpty()){
                ListCrcs.forEach((crc) ->{
                    Long idCliente = getLongValue(crc.get("fk_id_cliente"));
                    Cliente cliente = iCliente.findById(idCliente).orElse(null);
                    Optional<Crc> crcExistente = Optional.ofNullable(iCrc.findByCliente(cliente));
                    if (!crcExistente.isPresent()){
                        Object cicloObj = crc.get("n_ciclo");
                        Long idCiclo = null;
                        if (cicloObj != null && !cicloObj.toString().isBlank()) {
                            idCiclo = Long.valueOf(cicloObj.toString());
                        }
                        Ciclo ciclo = idCiclo != null ? iCiclo.findById(idCiclo).orElse(null) : null;

                        Object rutaObj = crc.get("n_ruta");
                        Long idRuta = null;
                        if (rutaObj != null && !rutaObj.toString().isBlank()) {
                            idRuta = Long.valueOf(rutaObj.toString());
                        }
                        Ruta ruta = idRuta != null ? iRuta.findById(idRuta).orElse(null) : null;

                        // n_consecutivo
                        Object consecutivoObj = crc.get("n_consecutivo");
                        Integer consecutivo = null;
                        if (consecutivoObj != null && !consecutivoObj.toString().isBlank()) {
                            consecutivo = Integer.valueOf(consecutivoObj.toString());
                        }
                        Crc crcToSave = Crc.builder()
                                .idCrcExternalBd((Integer) crc.get("id_crc"))
                                .cliente(cliente)
                                .ciclo(ciclo)
                                .ruta(ruta)
                                .consecutivo(consecutivo)
                                .user(user)
                                .build();
                        crcsForSave.add(crcToSave);
                    }
                });
                iCrc.saveAll(crcsForSave);
                response.put("status",200);
            }
        }catch (Exception e){
            log.error("Error en setLecturas: {}", e.getMessage(), e);
            response.put("status", 500);
            response.put("error", e.getMessage());
        }




        return ResponseEntity.ok().body(response);

    }
    private Long getLongValue(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).longValue() : null;
    }
}
