package com.springboot.aldiabackjava.repositories.lecturasCriticas;

import com.springboot.aldiabackjava.models.lecturasCriticas.MultimediaCriticaLecturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IMultimediaCriticas extends JpaRepository<MultimediaCriticaLecturas, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM MultimediaCriticaLecturas")
    void deleteAllMultimediaCriticas();
}
