package com.springboot.aldiabackjava.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.userModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historial_lecturas")
public class HistorialLecturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lectura_historial")
    private Long idLectura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    private Integer lectura;

}
