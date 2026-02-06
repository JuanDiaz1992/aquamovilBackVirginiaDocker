package com.springboot.aldiabackjava.repositories.lecturas;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ILecturas extends JpaRepository<Lectura, Long> {
    <Optional> Lectura findByCliente(Cliente cliente);

    @Modifying
    @Transactional
    @Query("DELETE FROM Lectura")
    void deleteAllLecturas();

    List<Lectura> findByCliente_IdClienteIn(List<Long> ids);
}
