package com.springboot.aldiabackjava.repositories;

import com.springboot.aldiabackjava.models.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ICiclo extends JpaRepository<Ciclo, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Ciclo")
    void deleteAllCiclo();
}
