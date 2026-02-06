package com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.MultimediaAutorizaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IMultimediaAutorizaciones extends JpaRepository<MultimediaAutorizaciones, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM MultimediaAutorizaciones")
    void deleteAllMultimediaAutorizaciones();
}
