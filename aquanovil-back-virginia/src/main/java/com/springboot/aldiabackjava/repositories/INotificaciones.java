package com.springboot.aldiabackjava.repositories;

import com.springboot.aldiabackjava.models.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificaciones extends JpaRepository<Notificaciones, Long> {
}
