package com.springboot.aldiabackjava.models.lecturas;

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
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lecturas")
public class Lectura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lectura")
    private Long idLectura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    private Integer lectura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_causal", referencedColumnName = "id_causal", nullable = true)
    private Causal causal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_observacion1", referencedColumnName = "id_observacion", nullable = true)
    private Observaciones observacion1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_observacion2", referencedColumnName = "id_observacion", nullable = true)
    private Observaciones observacion2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_observacion3", referencedColumnName = "id_observacion", nullable = true)
    private Observaciones observacion3;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="parametros", referencedColumnName = "id_parametros", nullable = true)
    private Parametros parametros;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_usuario", referencedColumnName = "id_user", nullable = false)
    private User user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha", nullable = false)
    private Date fecha;


    @OneToMany(mappedBy = "lectura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MultimediaLecturas> fotos;


}

