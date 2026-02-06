package com.springboot.aldiabackjava.controller;


import com.springboot.aldiabackjava.services.UserServices.publicServices.LoginService;
import com.springboot.aldiabackjava.services.UserServices.publicServices.RegisterService;
import com.springboot.aldiabackjava.services.UserServices.requestAndResponse.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Slf4j
public class PublicController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequest request){
        return loginService.loginUserService(request);
    }
}
