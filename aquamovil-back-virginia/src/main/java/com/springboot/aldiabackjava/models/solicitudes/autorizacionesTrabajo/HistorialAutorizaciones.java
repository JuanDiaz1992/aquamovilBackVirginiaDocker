package com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.userModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historial_autorizaciones")
public class HistorialAutorizaciones {
    @Id
    @Column(name = "id_historial_autorizaciones")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_autorizacion_trabajo")
    private AutorizacionTrabajo autorizacionTrabajo;

    @ManyToOne
    @JoinColumn(name = "fk_id_user", referencedColumnName = "id_user")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
