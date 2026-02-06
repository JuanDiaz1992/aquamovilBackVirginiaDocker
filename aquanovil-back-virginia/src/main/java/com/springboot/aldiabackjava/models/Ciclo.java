package com.springboot.aldiabackjava.models;

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
@Table(name = "ciclo")
public class Ciclo {
    @Id
    @Column(name = "id_ciclo")
    private Long idCiclo;
    private Integer ciclo;
}
