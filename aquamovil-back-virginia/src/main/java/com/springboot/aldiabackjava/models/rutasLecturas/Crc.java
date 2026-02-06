package com.springboot.aldiabackjava.models.rutasLecturas;

import com.springboot.aldiabackjava.models.Ciclo;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
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
@Table(name = "crc")
public class Crc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_crc")
    private Long idCrc;

    @Column(name = "id_crc_external_bd")
    private int idCrcExternalBd;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_usuario")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_ciclo", nullable = true)
    private Ciclo ciclo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_ruta", nullable = true)
    private Ruta ruta;

    @Column(name = "n_consecutivo", nullable = true)
    private Integer consecutivo;

}
