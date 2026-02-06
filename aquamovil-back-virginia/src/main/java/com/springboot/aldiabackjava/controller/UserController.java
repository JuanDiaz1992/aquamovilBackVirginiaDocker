package com.springboot.aldiabackjava.controller;

import com.springboot.aldiabackjava.config.JwtInterceptor;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.services.UserServices.AuthService;
import com.springboot.aldiabackjava.services.UserServices.desktopServices.CargoServices;
import com.springboot.aldiabackjava.services.UserServices.desktopServices.PermisosServices;
import com.springboot.aldiabackjava.services.UserServices.publicServices.RegisterService;
import com.springboot.aldiabackjava.services.UserServices.requestAndResponse.RegisterRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springboot.aldiabackjava.services.UserServices.requestAndResponse.ChangePasswordRequest;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final AuthService authService;
    private final RegisterService registerService;
    private final CargoServices cargoServices;
    private final PermisosServices permisosServices;
    private final JwtInterceptor jwtInterceptor;

    @GetMapping("")
    public ResponseEntity<User> getUser() {
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/users solicitado por {}", user.getUsername());
        return ResponseEntity.ok(authService.getUserService());
    }

    @PutMapping("/edit/picture")
    public ResponseEntity<Map<String,Object>> changeProfilePicture(@RequestBody Map<String, String> picture){
        User user = jwtInterceptor.getCurrentUser();
        log.info("PUT /api/v1/users/edit/picture solicitado por {}", user.getUsername());
        String photoBase64  = picture.get("photo");
        return authService.changerPictureProfilService(photoBase64);
    }

    @DeleteMapping("/delete/picture")
    public ResponseEntity<String> deleteProfilePicture(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /api/v1/users/delete/picture solicitado por {}", user.getUsername());
        return authService.deleteProfilePictureService();
    }

    @PutMapping("/edit/password")
    public ResponseEntity<Map<String,String>> changePassword(@RequestBody ChangePasswordRequest request){
        User user = jwtInterceptor.getCurrentUser();
        log.info("PUT /api/v1/users/edit/password solicitado por {}", user.getUsername());
        return authService.changePasswordService(request);
    }

    @PostMapping("/edit/desktop/password")
    public ResponseEntity<Map<String,Object>> changePasswordFromDesk(@RequestBody Map<String, Object> request){
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/users/edit/desktop/password solicitado por {}", user.getUsername());
        return authService.changePasswordServiceFromDesktop(request);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> register(@RequestBody RegisterRequest request){
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/users/register solicitado por {}", user.getUsername());
        return registerService.registerUserService(request);
    }

    @GetMapping("/getrusers")
    public ResponseEntity<Map<String,Object>> getAllUsers(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/users/getrusers solicitado por {}", user.getUsername());
        return authService.getAllUsers();
    }

    @GetMapping("/getcargos")
    public ResponseEntity<Map<String, Object>> getAllCargos(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/users/getcargos solicitado por {}", user.getUsername());
        return cargoServices.getAllCargos();
    }

    @GetMapping("/getopciones")
    public ResponseEntity<Map<String, Object>> getAllOptions(){
        User user = jwtInterceptor.getCurrentUser();
        log.info("GET /api/v1/users/getopciones solicitado por {}", user.getUsername());
        return permisosServices.getAllOpciones();
    }

    @PostMapping("/addpermisos")
    public ResponseEntity<Map<String, Object>> addPermisos(@RequestBody Map<String, Object> request ) {
        User user = jwtInterceptor.getCurrentUser();
        log.info("POST /api/v1/users/addpermisos solicitado por {}", user.getUsername());
        return permisosServices.insertPermisoService(request);
    }

    @DeleteMapping("delete/{idUser}")
    public ResponseEntity<Map<String,Object>> deleteUser(@PathVariable Long idUser){
        User user = jwtInterceptor.getCurrentUser();
        log.info("DELETE /api/v1/users/deleteuser {},  solicitado por {}", user.getUsername(), idUser);
        return authService.deleteUser(idUser);
    }
}
