package com.springboot.aldiabackjava.services.AquaMovilServices.desktopServices.clientes;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.rutasLecturas.Crc;
import com.springboot.aldiabackjava.repositories.rutasLecturas.ICrc;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrcDesktopServices {
    private final ICrc iCrc;
    private final Conversores conversores;

    public ResponseEntity<Map<String, Object>> getAllCrc() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> crcList = iCrc.findAll().stream().map(crc -> {
                Map<String, Object> tempCrc = new HashMap<>();
                if (crc.getCliente() != null) {
                    tempCrc.put("idCliente", crc.getCliente().getIdCliente());
                    tempCrc.put("actualConsecutivo", crc.getCliente().getConsecutivo());

                    if (crc.getCliente().getRuta() != null) {
                        tempCrc.put("actualRuta", crc.getCliente().getRuta().getIdRuta());

                        if (crc.getCliente().getRuta().getCiclo() != null) {
                            tempCrc.put("actualCiclo", crc.getCliente().getRuta().getCiclo().getIdCiclo());
                        } else {
                            tempCrc.put("actualCiclo", null);
                        }
                    } else {
                        tempCrc.put("actualRuta", null);
                        tempCrc.put("actualCiclo", null);
                    }
                } else {
                    tempCrc.put("idCliente", null);
                    tempCrc.put("actualConsecutivo", null);
                    tempCrc.put("actualRuta", null);
                    tempCrc.put("actualCiclo", null);
                }
                tempCrc.put("newCiclo", crc.getCiclo() != null ? crc.getCiclo().getIdCiclo() : null);
                tempCrc.put("newRuta", crc.getRuta() != null ? crc.getRuta().getIdRuta() : null);
                tempCrc.put("newConsecutivo", crc.getConsecutivo());
                tempCrc.put("idUsuario", crc.getUser() != null ? crc.getUser().getIdUser() : null);

                return tempCrc;
            }).toList();
            response.put("total", crcList.size());
            response.put("data", crcList);
            response.put("status", 200);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", 400);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<byte[]> exportCrcrScv() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024); // Buffer inicial de 1MB

            // BOM para UTF-8
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);

            try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                // Escribir encabezados
                writer.write("IdCliente,IdCiclo,IdRuta,NuevoCiclo,NuevaRuta,NuevoConsecutivo,idUsuario\n");

                // Procesar todos los clientes (versión simplificada sin paginación)
                List<Crc> crcs = iCrc.findAll();

                // Procesar cada cliente
                for (Crc crc : crcs) {

                    // Construir línea CSV
                    writer.write(
                             conversores.safe(crc.getCliente().getIdCliente()) + "," +
                                    conversores.safe(crc.getCliente().getRuta().getCiclo().getIdCiclo()) + "," +
                                    conversores.safe(crc.getCliente().getRuta().getIdRuta()) + "," +
                                    conversores.safe(crc.getCiclo() != null ? crc.getCiclo().getCiclo() : null) + "," +
                                    conversores.safe(crc.getRuta() !=null ? crc.getRuta().getIdRuta() : null) + "," +
                                    conversores.safe(crc.getConsecutivo() != null ? crc.getConsecutivo() : null) + "," +
                                    conversores.safe(crc.getUser().getIdUser()) + "\n"

                    );
                }

                writer.flush();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=solicitudescrc.csv")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(out.toByteArray());

        } catch (Exception e) {
            log.error("Error en exportación CSV", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
