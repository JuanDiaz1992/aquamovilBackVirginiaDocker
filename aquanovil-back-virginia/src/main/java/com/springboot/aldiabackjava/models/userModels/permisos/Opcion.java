package com.springboot.aldiabackjava.models.userModels.permisos;

import com.springboot.aldiabackjava.models.userModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "opcion")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcion")
    private Long idOpcion;

    @Column(name = "nombre_proceso")
    private String nombreProceso;

    @ManyToOne
    @JoinColumn(name ="fk_id_proceso", referencedColumnName = "id_proceso")
    private Proceso proceso;

}
