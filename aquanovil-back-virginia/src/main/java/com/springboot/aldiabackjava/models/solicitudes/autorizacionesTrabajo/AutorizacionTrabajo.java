package com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.MultimediaPreSolicitudes;
import com.springboot.aldiabackjava.models.userModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "autorizacion_trabajo")
public class AutorizacionTrabajo {
    @Id
    @Column(name = "id_ot")
    private Long idOt;

    @Column(name = "id_vt")
    private Integer idVt;

    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    private Date fecha;

    @Column(length = 1000)
    private String observacion;

    @Column(name="notas_solicitud", length = 1000)
    private String notasSoicitud;

    @Column(name = "vr_cotizacion")
    private Integer vrCotizacion;

    @Column(name = "vr_ot")
    private Integer vrOt;

    @OneToMany(mappedBy = "autorizacionTrabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<MultimediaAutorizaciones> multimediaAutorizaciones;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_user", referencedColumnName = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "closed_by")
    private User cerradoPor;

    @OneToMany(mappedBy = "autorizacionTrabajo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HistorialAutorizaciones> historial = new ArrayList<>();

    @Column(name="documento_de_quien_atiende")
    private String documentoDeQuienAtiende;

    @Lob
    @Column(name = "firma", columnDefinition = "LONGTEXT")
    private String firma;

    @Column(length = 1000, name="notas_operario")
    private String notasOperario;

    @Column(name = "fecha_realizacion")
    private Date fechaRealizacion;

    private Boolean sincronizado = false;

    @Column(name = "fecha_asignacion")
    private Date fechaDeAsignacion;

    @Column(name = "fecha_ultima_notificacion")
    private Date fechaUltimaNotificacion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WorkOrderStatus status = WorkOrderStatus.PENDIENTE;

    public enum WorkOrderStatus {
        PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA
    }

}
