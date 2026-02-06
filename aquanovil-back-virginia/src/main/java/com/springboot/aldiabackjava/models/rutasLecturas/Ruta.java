package com.springboot.aldiabackjava.models.rutasLecturas;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "rutas")
public class Ruta {
    @Id
    @Column(name = "id_ruta")
    private Long idRuta;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_ciclo", referencedColumnName = "id_ciclo")
    private com.springboot.aldiabackjava.models.Ciclo Ciclo;
    private Integer ruta;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    private Boolean estado = false;
}
