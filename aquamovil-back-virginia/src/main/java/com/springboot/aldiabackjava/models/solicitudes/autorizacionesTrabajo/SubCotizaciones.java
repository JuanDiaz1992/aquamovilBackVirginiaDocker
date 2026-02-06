package com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.solicitudes.ProductosYServicios;
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
@Table(name = "sub_cotizacion")
public class SubCotizaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Long idCotizacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_ot", referencedColumnName = "id_ot")
    private AutorizacionTrabajo autorizacionTrabajo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_productos_y_servicios", referencedColumnName = "id_item")
    private ProductosYServicios productosYServicios;

    private Integer cantidad;

    @Column(name = "vr_unitario")
    private Double vrUnitario;

    private Double valor;

    @Column(name = "cantidad_anterior")
    private Double cantidadAnterior;

    @Column(name = "vr_anterior")
    private Double valorAnterior;

    private String descripcion;

    @Column(name = "id_cargo")
    private Integer idCargo;

    @Column(name = "id_servicio")
    private Integer idServicio;

    private Integer asume;

    @Column(name = "documento_operario")
    private Integer documentoOperario;

}
