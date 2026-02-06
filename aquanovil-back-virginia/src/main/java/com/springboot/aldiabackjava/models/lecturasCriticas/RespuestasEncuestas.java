package com.springboot.aldiabackjava.models.lecturasCriticas;

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
@Table(name = "respuestas_encuestas")
public class RespuestasEncuestas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long idRespuesta;

    private String respuesta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_pregunta", referencedColumnName = "id_pregunta")
    private PreguntaEncuesta preguntaEncuesta;

}
