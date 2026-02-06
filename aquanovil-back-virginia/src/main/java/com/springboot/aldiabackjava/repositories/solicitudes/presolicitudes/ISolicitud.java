package com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes;

import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.Solicitud;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ISolicitud extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Solicitud")
    void deleteAllSolicitudes();
}
