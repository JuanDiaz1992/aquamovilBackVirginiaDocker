package com.springboot.aldiabackjava.repositories.rutasLecturaCritica;

import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.rutasLecturas.Ruta;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRutaLecturaCritica extends JpaRepository<RutaLecturaCritica, Long> {
    List<Ruta> findByUser(User user);

    @Query("SELECT r FROM RutaLecturaCritica r WHERE r.user.idUser = :idUser")
    List<RutaLecturaCritica> findByUserId(@Param("idUser") Long idUser);

    @Modifying
    @Transactional
    @Query("DELETE FROM RutaLecturaCritica")
    void deleteAllRutaLecturaCritica();
}
