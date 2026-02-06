package com.springboot.aldiabackjava.services.UserServices.requestAndResponse;

import com.springboot.aldiabackjava.models.userModels.Cargo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private Long idCargo;
    private String name;
    private Long document;
    private String email;
}
