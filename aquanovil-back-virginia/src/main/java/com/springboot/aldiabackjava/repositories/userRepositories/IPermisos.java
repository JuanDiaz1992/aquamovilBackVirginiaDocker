package com.springboot.aldiabackjava.repositories.userRepositories;

import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.userModels.permisos.Permiso;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPermisos extends JpaRepository<Permiso, Long> {
    Permiso findByUser(User user);


    @Query("SELECT p FROM Permiso p WHERE p.user.idUser = :idUser AND p.opcion.idOpcion = :idOpcion")
    Permiso findPermisoByUserAndOpcion(@Param("idUser") Long idUser, @Param("idOpcion") Long idOpcion);

    List<Permiso> findAllByUser(User user);
}
