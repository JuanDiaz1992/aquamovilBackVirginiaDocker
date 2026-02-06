package com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.SubCotizaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ISubCotizaciones extends JpaRepository<SubCotizaciones, Long> {

    List<SubCotizaciones> findByAutorizacionTrabajo(AutorizacionTrabajo autorizacionTrabajo);

    @Query("""
    SELECT 
        s.autorizacionTrabajo.idOt,
        s.productosYServicios.idItem,
        s.idCotizacion,
        s.cantidad,
        s.vrUnitario,
        s.valor,
        s.valorAnterior,
        s.descripcion,
        s.idCargo,
        s.idServicio,
        s.asume,
        s.documentoOperario
    FROM SubCotizaciones s
    WHERE s.autorizacionTrabajo = :autorizacion
""")
    List<Object[]> findReducidasByAutorizacion(@Param("autorizacion") AutorizacionTrabajo autorizacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM SubCotizaciones")
    void deleteAllSubCotizaciones();
}
