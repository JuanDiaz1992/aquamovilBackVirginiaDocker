package com.springboot.aldiabackjava.models.lecturasCriticas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.models.lecturas.MultimediaLecturas;
import com.springboot.aldiabackjava.models.userModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "critica_lectura")
public class CriticaLectura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_critica_lectura")
    private Long idCriticaLectura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_usuario", referencedColumnName = "id_user", nullable = false)
    private User user;

    private Integer lectura;

    private String atendio;

    @Column(name="documento_atendio")
    private String documentoAntendio;

    private String telefono;

    private String motivo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @OneToMany(mappedBy = "criticaLectura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MultimediaCriticaLecturas> multimedia;

    @Column(length = 1000)
    private String observaciones;

    private String respuestas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_solicitud_critica_lectura", referencedColumnName = "id_solicitud_critica_lectura", nullable = false)
    private SolicitudLecturaCritica solicitudLecturaCritica;



}
