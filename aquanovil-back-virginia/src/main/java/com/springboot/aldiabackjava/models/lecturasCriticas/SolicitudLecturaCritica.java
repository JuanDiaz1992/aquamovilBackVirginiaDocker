package com.springboot.aldiabackjava.models.lecturasCriticas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.HistorialLecturas;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Causal;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import com.springboot.aldiabackjava.repositories.IHistorialLecturas;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "solicitudes_criticas")
public class SolicitudLecturaCritica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_critica_lectura")
    private Long idSolicitudCriticaLectura;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    private boolean completada = false;

    private String motivo;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Date fechaCreacion;

    private Integer consumo;

    private Integer lectura;

    private Boolean acueducto;

    private Boolean alcantarillado;

    private Boolean aseo;

    private Integer promedio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_causal", referencedColumnName = "id_causal", nullable = true)
    private Causal causal;

    @Column(name="tiene_medidor")
    private Boolean tieneMedidor;

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
    @JoinColumn(name="fk_id_causal_anterior", referencedColumnName = "id_causal", nullable = true)
    private Causal causalAnt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_observacion1_anterior", referencedColumnName = "id_observacion", nullable = true)
    private Observaciones observacion1Ant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_observacion2_anterior", referencedColumnName = "id_observacion", nullable = true)
    private Observaciones observacion2Ant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fk_id_observacion3_anterior", referencedColumnName = "id_observacion", nullable = true)
    private Observaciones observacion3Ant;


}
