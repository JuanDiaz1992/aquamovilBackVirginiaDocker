package com.springboot.aldiabackjava.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private String tipo;
    private String mensaje;
    private Long referenciaId;
}