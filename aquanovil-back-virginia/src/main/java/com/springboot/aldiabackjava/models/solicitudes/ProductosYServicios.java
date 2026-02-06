package com.springboot.aldiabackjava.models.solicitudes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "productos_y_servicios")
public class ProductosYServicios {
    @Id
    @Column(name = "id_item")
    private Long idItem;

    private String codigo;

    @Column(name="nombre_item")
    private String nombreItem;

    private String unidad;

    @Column(name="valor_unitario")
    private Double valorUnitario;

    private Integer gravado;

    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "id_cargo")
    private Integer idCargo;

    @Column(name = "id_cargo_ac")
    private Integer idCargoAc;

    @Column(name = "id_cargo_ai")
    private Integer idCargoAI;

    @Column(name = "id_cargo_as")
    private Integer idCargoAs;

    @Column(name = "fechaActualizacion")
    private Date fechaActualizacion;

    @Column(name = "saleAlmacen")
    private Boolean saleAlmacen;

    private Boolean activo;
}
