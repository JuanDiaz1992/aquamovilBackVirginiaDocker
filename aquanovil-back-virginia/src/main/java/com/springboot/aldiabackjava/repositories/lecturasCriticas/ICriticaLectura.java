package com.springboot.aldiabackjava.repositories.lecturasCriticas;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturas.Lectura;
import com.springboot.aldiabackjava.models.lecturasCriticas.CriticaLectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICriticaLectura extends JpaRepository<CriticaLectura, Long> {
    <Optional> CriticaLectura findByCliente(Cliente cliente);

    @Query("SELECT c FROM CriticaLectura c WHERE c.cliente.idCliente IN :clienteIds")
    List<CriticaLectura> findByClienteIds(@Param("clienteIds") List<Long> clienteIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM CriticaLectura")
    void deleteAllCriticaLectura();

}
