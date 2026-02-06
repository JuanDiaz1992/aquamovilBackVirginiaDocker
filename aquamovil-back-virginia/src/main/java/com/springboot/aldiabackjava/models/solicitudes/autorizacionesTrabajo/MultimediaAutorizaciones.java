package com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.Solicitud;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "multimedia_de_autorizaciones")
public class MultimediaAutorizaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto_solicitud")
    private Long idFotoSolicitud;

    @Column(name = "ruta_foto_solicitud")
    private String rutaFotoSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_ot", referencedColumnName = "id_ot", nullable = false)
    private AutorizacionTrabajo autorizacionTrabajo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;
}
