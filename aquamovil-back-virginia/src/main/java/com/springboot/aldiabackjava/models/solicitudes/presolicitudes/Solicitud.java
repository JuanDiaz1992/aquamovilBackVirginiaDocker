package com.springboot.aldiabackjava.models.solicitudes.presolicitudes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
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
@Table(name = "pre_solicitud_trabajo")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ot")
    private Long idOt;
    @Column(name = "id_solicitud")
    private Integer idSolicitud;
    @Column(name = "tipo_solicitud")
    private Integer tipoSolicitud;
    @Column(name = "tipo_solicitud2")
    private Integer idTipoSolicitud2;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;
    @Column(name = "id_periodo")
    private Integer idPerido;
    @Column(name = "id_presentacion")
    private Integer idPresentacion;
    private Date fecha;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String notas;
    @Column(name = "nombre_solicitud")
    private String nombreSolicitud;
    private String telefono;
    private String cedula;
    private String correo;
    private Boolean completado;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<MultimediaPreSolicitudes> multimediaPreSolicitudesList;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_user", referencedColumnName = "id_user")
    private User user;

    //Datos después de completada
    @Column(name="documento_de_quien_atiende")
    private String documentoDeQuienAtiende;

    @Lob
    @Column(name = "firma", columnDefinition = "LONGTEXT")
    private String firma;


    @Lob
    @Column(name = "notas_operario", columnDefinition = "LONGTEXT")
    private String notasOperario;

    private Boolean sincronizado = false;


}
