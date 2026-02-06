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
@Table(name = "corporativo")
public class Corporativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corporativo")
    private Long idCorporativo;

    @Column(name="nombre_corporativo")
    private String nombreCorporativo;

    private String nit;

    private String logo1;

    private String logo2;

}
