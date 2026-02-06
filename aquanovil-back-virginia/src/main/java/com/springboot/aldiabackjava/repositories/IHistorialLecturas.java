package com.springboot.aldiabackjava.repositories;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.HistorialLecturas;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IHistorialLecturas extends CrudRepository<HistorialLecturas, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM HistorialLecturas")
    void deleteAllHistorialLecturas();

    List<HistorialLecturas> findByCliente_IdClienteIn(List<Long> ids);


}
