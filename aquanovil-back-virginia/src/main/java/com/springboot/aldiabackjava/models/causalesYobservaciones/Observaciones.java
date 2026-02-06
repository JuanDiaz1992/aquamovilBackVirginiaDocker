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
@Table(name = "observaciones")
public class Observaciones {
    @Id
    @Column(name = "id_observacion")
    private Long idObservacion;
    private String descripcion;
    @Column(name = "requiere_foto")
    private Boolean requiereFoto;
}
