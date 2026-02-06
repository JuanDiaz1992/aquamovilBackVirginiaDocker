package com.springboot.aldiabackjava.models.Clientes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.userModels.User;
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
@Table(name = "clientes")
public class Cliente {
    @Id
    @Column(name = "id_cliente")
    private Long idCliente;

    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_ruta_asociada", referencedColumnName = "id_ruta", nullable = true)
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_ruta_lectura_critica", referencedColumnName = "id_ruta_lectura_critica", nullable = true)
    private RutaLecturaCritica rutaLecturaCritica;

    private Integer consecutivo;

    private String direccion;

    @Column(name = "n_medidor")
    private String nMedidor;

    @Column(name = "cat_medidor")
    private Integer catMedidor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_causal_anterior", referencedColumnName = "id_causal", nullable = true)
    private Causal causal;

    @Column(name = "obs_anterior")
    private Integer obsAnterior;

    @Column(name = "ultima_lectura")
    private Integer ultimaLectura;

    private Integer promedio;

    @Column(name = "lectura_completada")
    private Boolean lecturaCompletada = false;

    @Column(name = "critica_lectura_completada")
    private Boolean criticaLecturaCompletada = false;

    @Column(name="autorizacion_trabajo_completada")
    private Boolean autorizacionTrabajoCompletada = false;

    @Column(name="pre_solicitud_completada")
    private Boolean preSolicitudCompletada = false;

    @Column(name = "id_uso", nullable = true)
    private Integer idUso;

    @Column(name = "categoria", nullable = true)
    private Integer categoria;


}
