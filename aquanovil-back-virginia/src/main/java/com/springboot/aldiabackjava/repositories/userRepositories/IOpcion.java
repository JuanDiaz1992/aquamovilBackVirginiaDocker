package com.springboot.aldiabackjava.repositories.userRepositories;

import com.springboot.aldiabackjava.models.userModels.permisos.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOpcion extends JpaRepository<Opcion, Long> {
}
