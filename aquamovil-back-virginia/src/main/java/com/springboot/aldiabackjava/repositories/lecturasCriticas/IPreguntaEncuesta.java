package com.springboot.aldiabackjava.repositories.lecturasCriticas;

import com.springboot.aldiabackjava.models.lecturasCriticas.PreguntaEncuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IPreguntaEncuesta extends JpaRepository<PreguntaEncuesta, Long> {
}
