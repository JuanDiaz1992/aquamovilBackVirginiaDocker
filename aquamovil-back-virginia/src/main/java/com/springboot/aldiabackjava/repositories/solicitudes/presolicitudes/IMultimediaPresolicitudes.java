package com.springboot.aldiabackjava.repositories.solicitudes.presolicitudes;

import com.springboot.aldiabackjava.models.solicitudes.presolicitudes.MultimediaPreSolicitudes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IMultimediaPresolicitudes extends JpaRepository<MultimediaPreSolicitudes, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM MultimediaPreSolicitudes")
    void deleteAllMultimediaPresolicitudes();
}
