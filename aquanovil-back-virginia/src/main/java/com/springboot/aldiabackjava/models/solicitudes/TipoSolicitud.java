package com.springboot.aldiabackjava.models.solicitudes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tipo_solicitud")
public class TipoSolicitud {
    @Id
    @Column(name = "id_tipo_solicitud")
    private Long idTipoSolicitud;
    @Column(name = "tipo_solicitud")
    private String tipoSolicitud;
    private Integer direccionamiento;
    @Column(name = "carga_funcional")
    private Integer cargoFuncional;
    private Boolean referencia = false;
    private Boolean comercial = false;

}
