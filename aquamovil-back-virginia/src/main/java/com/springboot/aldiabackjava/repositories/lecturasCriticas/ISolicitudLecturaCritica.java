package com.springboot.aldiabackjava.repositories.lecturasCriticas;

import com.springboot.aldiabackjava.models.Clientes.Cliente;
import com.springboot.aldiabackjava.models.lecturasCriticas.SolicitudLecturaCritica;
import com.springboot.aldiabackjava.models.rutasCriticaLectura.RutaLecturaCritica;
import com.springboot.aldiabackjava.models.userModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ISolicitudLecturaCritica extends JpaRepository<SolicitudLecturaCritica, Long> {
    List<SolicitudLecturaCritica> findByCliente(Cliente cliente);

    @Modifying
    @Transactional
    @Query("DELETE FROM SolicitudLecturaCritica")
    void deleteAllSolicitudLecturaCritica();

    List<SolicitudLecturaCritica> findByCliente_RutaLecturaCritica(RutaLecturaCritica ruta);

    @Query(value = """
    SELECT 
        s.id_solicitud_critica_lectura AS idSolicitud,
        c.id_cliente AS idCliente,
        c.nombre AS nombre,
        r.ruta AS ruta,
        cl.ciclo AS ciclo,
        s.motivo AS motivo,
        s.completada AS completada
    FROM solicitudes_criticas s
    JOIN clientes c ON s.fk_id_cliente = c.id_cliente
    JOIN ruta_lectura_critica r ON c.fk_ruta_lectura_critica = r.id_ruta_lectura_critica
    JOIN ciclo cl ON r.fk_id_ciclo = cl.id_ciclo
""", nativeQuery = true)
    List<Map<String, Object>> getSolicitudesCriticaInfo();

    @Query("""
    SELECT DISTINCT c.rutaLecturaCritica
    FROM SolicitudLecturaCritica s
    JOIN s.cliente c
    WHERE c.rutaLecturaCritica IS NOT NULL
""")
    List<RutaLecturaCritica> findDistinctRutasConSolicitudes();
}
