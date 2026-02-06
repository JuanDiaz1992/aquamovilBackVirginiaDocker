package com.springboot.aldiabackjava.services.UserServices.publicServices;

import com.springboot.aldiabackjava.models.userModels.User;
import com.springboot.aldiabackjava.repositories.userRepositories.ICargo;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import com.springboot.aldiabackjava.services.UserServices.requestAndResponse.RegisterRequest;
import com.springboot.aldiabackjava.utils.Conversores;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository iUserRepository;
    private final Conversores conversores;
    private final ICargo iCargo;


    public ResponseEntity<Map<String,Object>> registerUserService(RegisterRequest request) {
        log.info(request.toString());
        Map <String,Object> result = new HashMap<>();
        try{
            User existUser = iUserRepository.findById(request.getDocument()).orElse(null);
            if (existUser != null){
                result.put("status", 409);
                result.put("message","El usuario ya existe");
                return ResponseEntity.badRequest().body(result);
            }
            User existUsername = iUserRepository.findByUsername(request.getUsername()).orElse(null);
            if (existUsername != null){
                result.put("status","409");
                result.put("message","El username ya existe");
                return ResponseEntity.badRequest().body(result);
            }
            String pass = passwordEncoder.encode(request.getPassword());
            User user = User.builder()
                    .idUser(request.getDocument())
                    .username(request.getUsername())
                    .password(pass)
                    .cargo(iCargo.findById(request.getIdCargo()).orElse(null))
                    .email(request.getEmail())
                    .name(request.getName()).build();
            log.info(user.toString());
            iUserRepository.save(user);
            result.put("status", 200);
            result.put("message","Usuario registrado correctamente");
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            result.put("status","409");
            result.put("message","Ah ocurrido un error, intentalo de nuevo más tarde");
            return ResponseEntity.badRequest().body(result);
        }

    }
}
