package com.springboot.aldiabackjava.repositories.solicitudes.autorizacionesTrabajo;

import com.springboot.aldiabackjava.models.solicitudes.autorizacionesTrabajo.AutorizacionTrabajo;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IAutorizacionTrabajo extends JpaRepository<AutorizacionTrabajo, Long> {
    List<AutorizacionTrabajo> findByUser(User user);

    AutorizacionTrabajo findByIdOt(Long idOt);

    @Query("SELECT a FROM AutorizacionTrabajo a WHERE a.status IS NULL OR a.status = 'PENDIENTE'")
    List<AutorizacionTrabajo> findByCompletadoFalseOrNull();

    @Modifying
    @Transactional
    @Query("DELETE FROM AutorizacionTrabajo")
    void deleteAllAutorizacionTrabajo();
}
