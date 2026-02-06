package com.springboot.aldiabackjava.models.rutasCriticaLectura;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.aldiabackjava.models.Ciclo;
import com.springboot.aldiabackjava.models.userModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ruta_lectura_critica")
public class RutaLecturaCritica {
    @Id
    @Column(name = "id_ruta_lectura_critica")
    private Long idRutaLecturaCritica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_ciclo", referencedColumnName = "id_ciclo")
    private Ciclo ciclo;

    private Integer ruta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;
}
