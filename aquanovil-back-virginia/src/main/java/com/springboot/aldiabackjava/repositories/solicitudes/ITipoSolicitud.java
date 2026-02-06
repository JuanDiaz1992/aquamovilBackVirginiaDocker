package com.springboot.aldiabackjava.repositories.solicitudes;

import com.springboot.aldiabackjava.models.solicitudes.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoSolicitud extends JpaRepository<TipoSolicitud, Long> {
}
