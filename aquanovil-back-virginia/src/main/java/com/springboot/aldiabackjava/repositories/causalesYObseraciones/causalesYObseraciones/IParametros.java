package com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones;

import com.springboot.aldiabackjava.models.causalesYobservaciones.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IParametros extends JpaRepository<Parametros, Long> {}
