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
@Table(name = "causales")
public class Causal {
    @Id
    @Column(name = "id_causal")
    private Long idCausal;
    private String nombre;
    @Column(name = "requiere_foto")
    private Boolean requiereFoto;
    @Column(name = "requiere_observacion")
    private Boolean requiereObservacion;
}
