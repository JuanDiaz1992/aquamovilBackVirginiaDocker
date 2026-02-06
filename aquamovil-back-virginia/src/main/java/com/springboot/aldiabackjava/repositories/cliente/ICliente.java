package com.springboot.aldiabackjava.repositories.cliente;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICliente extends JpaRepository<Cliente, Long> {
    List<Cliente> findByRuta_IdRuta(Long idRuta);

    @Query(value = "SELECT * FROM clientes ORDER BY id_cliente LIMIT ?1 OFFSET ?2",
            nativeQuery = true)
    List<Cliente> findBatch(int limit, int offset);


    @Query("SELECT c FROM Cliente c WHERE c.ruta.idRuta IN :rutaIds")
    List<Cliente> findByRutaIds(@Param("rutaIds") List<Long> rutaIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cliente")
    void deleteAllCliente();
}
