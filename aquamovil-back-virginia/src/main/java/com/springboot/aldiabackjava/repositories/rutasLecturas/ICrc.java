package com.springboot.aldiabackjava.repositories.rutasLecturas;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.rutasLecturas.Crc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ICrc extends JpaRepository<Crc, Long> {
    <Optional> Crc findByCliente(Cliente cliente);
    @Modifying
    @Transactional
    @Query("DELETE FROM Crc")
    void deleteAllCrc();
}
