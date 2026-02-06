package com.springboot.aldiabackjava.services.UserServices.publicServices;
import com.springboot.aldiabackjava.JWT.JwtTokenService;
import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import com.springboot.aldiabackjava.services.UserServices.DataValidate;
import com.springboot.aldiabackjava.services.UserServices.requestAndResponse.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoginService {

    private final IUserRepository iUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final DataValidate dataValidate;

    public ResponseEntity<Map<String, Object>> loginUserService(LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        String message = "";
        int status = 0;
        String username = request.getUsername();

        try {
            String result = dataValidate.validateUsername(username);
            if (result != null) {
                message = result;
                status = 401;
                response.put("message", message);
                response.put("status", status);
                log.warn("Intento de login inválido para usuario '{}': {}", username, message);
                return ResponseEntity.badRequest().body(response);
            }

            User user;
            try {
                user = iUserRepository.findByUsername(username).orElseThrow();
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
            } catch (Exception e) {
                message = "Usuario o contraseña incorrectos";
                status = 401;
                response.put("message", message);
                response.put("status", status);
                log.warn("Login fallido para usuario '{}'", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = jwtTokenService.getToken(user);
            message = "Bienvenido " + user.getName();
            status = 200;

            log.info("Login exitoso para usuario '{}'", username);

            Map<String, Object> buildResponse = UtilsPublicServices.buildResponseLogin(user, token, message, status);
            return ResponseEntity.ok().body(buildResponse);

        } catch (Exception e) {
            message = "Error al iniciar sesión, valide con el administrador";
            status = 500;
            response.put("message", message);
            response.put("status", status);
            log.error("Error inesperado en login para usuario '{}': {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

