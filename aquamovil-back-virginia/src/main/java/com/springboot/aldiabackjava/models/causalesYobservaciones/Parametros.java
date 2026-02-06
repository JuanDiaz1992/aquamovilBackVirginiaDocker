package com.springboot.aldiabackjava.models.causalesYobservaciones;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parametros")
public class Parametros {
    @Id
    @Column(name = "id_parametros")
    private Long idParametros;
    private String valor;
    private String nombre;
}
