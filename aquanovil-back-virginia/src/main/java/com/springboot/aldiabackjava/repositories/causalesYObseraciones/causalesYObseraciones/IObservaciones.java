package com.springboot.aldiabackjava.repositories.causalesYObseraciones.causalesYObseraciones;

import com.springboot.aldiabackjava.models.causalesYobservaciones.Observaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IObservaciones extends JpaRepository<Observaciones, Long> {}
