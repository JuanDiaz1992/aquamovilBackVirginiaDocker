package com.springboot.aldiabackjava.services.UserServices;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DataValidate {

    public String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "El usuario no puede estar vacío";
        }
        String USER_REGEX = "^[A-Za-z0-9]+(-[A-Za-z0-9]+)?$";
        if (!username.matches(USER_REGEX)) {
            return "El formato del usuario no es válido";
        }
        return null;
    }

    public String validatePassword(String password){
        if (password == null) {
            return "La contraseña no puede estar vacía";
        }
        if (password.length() < 8){
            return "La nueva contraseña debe tener más de 8 caracteres";
        }
        String regex = "^(?=.*[A-Za-z])(?=.*\\d).+$";
        if (!password.matches(regex)){
            return "La nueva contraseña debe contener letras y números";
        }
        return null;
    }

    public String validateEmail(String email){
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || email.isEmpty()) {
            return "El correo no puede estar vacío";
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return "El formato del correo no es válido";
        }

        return null;
    }
}
