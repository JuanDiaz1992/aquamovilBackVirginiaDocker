package com.springboot.aldiabackjava.services.UserServices.publicServices;

import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.models.userModels.permisos.Permiso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UtilsPublicServices {

    public static Map<String, Object> buildResponseLogin(User user, String token, String message, int status) {
        if (!token.isEmpty()) {
            List<Object>opciones = new ArrayList<>();
            for(Permiso opcion : user.getPermisos()){
                opciones.add(opcion.getOpcion().getIdOpcion());
            }
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username",user.getUsername());
            response.put("idUser", user.getIdUser());
            response.put("name", user.getName());
            response.put("photo", user.getProfilePicture());
            response.put("cargo", user.getCargo().getNombreCargo());
            response.put("mail", user.getEmail());
            response.put("permisos", opciones);
            response.put("message", message);
            response.put("status", status);
            return response;
        }
        return null;
    }


}
