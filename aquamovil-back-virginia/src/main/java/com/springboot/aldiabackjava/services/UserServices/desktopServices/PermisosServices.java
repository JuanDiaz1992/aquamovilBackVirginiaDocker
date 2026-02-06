package com.springboot.aldiabackjava.services.UserServices.desktopServices;

import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.models.userModels.permisos.Opcion;
import com.springboot.aldiabackjava.models.userModels.permisos.Permiso;
import com.springboot.aldiabackjava.repositories.userRepositories.IOpcion;
import com.springboot.aldiabackjava.repositories.userRepositories.IPermisos;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PermisosServices {
    private final IPermisos iPermisos;
    private final IUserRepository iUserRepository;
    private final IOpcion iOpcion;
    private final Conversores conversores;

    public ResponseEntity<Map<String, Object>> insertPermisoService(Map<String, Object> solicitudPermiso){
        log.info(solicitudPermiso.toString());
        Map<String, Object> response = new HashMap<>();
        try{
            User user = iUserRepository.findById(conversores.getLongValue(solicitudPermiso.get("idUser"))).orElse(null);
            List<Long> listOptionsId = new ArrayList<>();
            List<?> permisos = (List<?>) solicitudPermiso.get("permisos");

            for (Object permiso : permisos) {
                if (permiso instanceof Integer) {
                    listOptionsId.add(((Integer) permiso).longValue());
                } else if (permiso instanceof Long) {
                    listOptionsId.add((Long) permiso);
                } else if (permiso instanceof String) {
                    listOptionsId.add(Long.parseLong((String) permiso));
                }
            }

            List<Permiso> permisoList = new ArrayList<>();
            List<Permiso> permisosExistente = iPermisos.findAllByUser(user);
            iPermisos.deleteAll(permisosExistente);
            for (Long optionId : listOptionsId) {
                Permiso permiso = iPermisos.findPermisoByUserAndOpcion(user.getIdUser(), optionId);
                if (permiso == null) {
                    Opcion opcion = iOpcion.findById(optionId).orElse(null);
                    if (opcion != null) {
                        Permiso permisoNuevo = Permiso.builder()
                                .user(user)
                                .opcion(opcion)
                                .build();
                        permisoList.add(permisoNuevo);
                    }
                }
            }

            if (!permisoList.isEmpty()) {
                iPermisos.saveAll(permisoList);
                response.put("message", "Permiso asignado correctamente");
                response.put("status", 200);
                return ResponseEntity.ok().body(response);
            } else {
                response.put("message", "No se asignaron permisos nuevos");
                response.put("status", 200);
                return ResponseEntity.ok().body(response);
            }
        }catch (Exception e){
            response.put("message", "Ah ocurrido un error, intentalo de nuevo más tarde");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getAllOpciones(){
        Map<String, Object> response = new HashMap<>();
        try{
            List<Opcion> opcionList = iOpcion.findAll();

            // Filtrar las opciones por proceso
            List<Opcion> aquaMovilOptions = opcionList.stream()
                    .filter(opcion -> "AquaMovil".equals(opcion.getProceso().getNombre()))
                    .collect(Collectors.toList());

            List<Opcion> aquaDeskOptions = opcionList.stream()
                    .filter(opcion -> "AquaDesk".equals(opcion.getProceso().getNombre()))
                    .collect(Collectors.toList());

            // Agregar ambas listas al mapa de respuesta
            response.put("aquaMovilOptions", aquaMovilOptions);
            response.put("aquaDeskOptions", aquaDeskOptions);
            response.put("status", 200);

            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            response.put("message","Ha ocurrido un error, intentalo más tarde");
            response.put("status", 400);
            return ResponseEntity.badRequest().body(response);
        }
    }


}
