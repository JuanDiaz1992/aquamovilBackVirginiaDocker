package com.springboot.aldiabackjava.repositories.rutasLecturas;

import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRuta extends JpaRepository<Ruta, Long> {
    List<Ruta> findByUser(User user);

    @Query("SELECT r FROM Ruta r WHERE r.user.idUser = :idUser")
    List<Ruta> findByUserId(@Param("idUser") Long idUser);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ruta")
    void deleteAllRuta();

    @Query("""
    SELECT DISTINCT c.ruta
    FROM Cliente c
    WHERE c.ruta IS NOT NULL
""")
    List<Ruta> findRutasConClientes();
}
