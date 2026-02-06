package com.springboot.aldiabackjava.services.UserServices;


import com.springboot.aldiabackjava.config.JwtInterceptor;

import com.springboot.aldiabackjava.models.userModels.User;

import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import com.springboot.aldiabackjava.services.UserServices.requestAndResponse.ChangePasswordRequest;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtInterceptor jwtInterceptor;
    private final DataValidate dataValidate;
    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final SavePicture savePicture;
    private final Conversores conversores;
    public User getUserService() {
        return jwtInterceptor.getCurrentUser();
    }


    public ResponseEntity<Map<String, Object>> changerPictureProfilService(String photo) {
        Map response =  new HashMap<>();
        byte[] decodedBytes = Base64.getDecoder().decode(photo);
        User user = jwtInterceptor.getCurrentUser();
        String finalPath = savePicture.changerPictureProfilService(user,decodedBytes);
        if (finalPath==null){
            response.put("message", "A ocurrido un error, intentelo de nuevo.");
            response.put("status", "400");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        response.put("message", "Cambio exitoso");
        response.put("status", 200);
        response.put("url",finalPath);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<String> deleteProfilePictureService() {
        User user = jwtInterceptor.getCurrentUser();
        if (user.getProfilePicture().equals("/img/sin_imagen.webp")){
            return ResponseEntity.ok().body("No se puede relizar esta acción");
        }
        try {
            Files.delete(Path.of("src/main/resources/static"+user.getProfilePicture()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.setProfilePicture("/img/sin_imagen.webp");
        iUserRepository.save(user);
        return ResponseEntity.ok().body("Foto eliminada");

    }

    public ResponseEntity<Map<String,String>> changePasswordService(ChangePasswordRequest request){
        User user = jwtInterceptor.getCurrentUser();
        Map<String,String> response = new HashMap<>();
        String morValidations = dataValidate.validatePassword(request.getNewPassword());
        if (morValidations != null){
            response.put("status","409");
            response.put("message",morValidations);
            return ResponseEntity.badRequest().body(response);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        iUserRepository.save(user);
        response.put("status","200");
        response.put("message","La contraseña se cambió correctamente");
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Map<String, Object>> getAllUsers(){
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> usersList = iUserRepository.findAll();
            response.put("totalUsuarios", usersList.size());
            response.put("users", usersList);
            response.put("status",200);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            response.put("message","Error al realizar la consulta");
            response.put("status",400);
            return ResponseEntity.badRequest().body(response);
        }
    }

    public ResponseEntity<Map<String,Object>> changePasswordServiceFromDesktop(Map<String, Object> request){
        User user = iUserRepository.findById(conversores.getLongValue(request.get("idUser"))).orElse(null);
        Map<String,Object> response = new HashMap<>();
        String newPassword = conversores.safe(request.get("password"));
        String morValidations = dataValidate.validatePassword(newPassword);
        if (morValidations != null){
            response.put("status","409");
            response.put("message",morValidations);
            return ResponseEntity.badRequest().body(response);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        iUserRepository.save(user);
        response.put("status",200);
        response.put("message","La contraseña se cambió correctamente");
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Map<String, Object>> deleteUser(Long idUser) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = iUserRepository.findById(idUser).orElse(null);
            if (user == null) {
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            iUserRepository.delete(user);
            response.put("status", 200);
            response.put("message", "Usuario eliminado correctamente");
            return ResponseEntity.ok().body(response);

        } catch (DataIntegrityViolationException e) {
            response.put("message", "No se puede eliminar el usuario porque está asociado a otros registros");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        } catch (Exception e) {
            response.put("message", "Error al eliminar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
