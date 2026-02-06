package com.springboot.aldiabackjava.models.lecturasCriticas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.aldiabackjava.models.Clientes.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "multimedia_critica_lecturas")
public class MultimediaCriticaLecturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto_lectura")
    private Long idFotoLectura;

    @Column(name = "ruta_foto_lectura")
    private String rutaFotoLectura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_critica_lectura", referencedColumnName = "id_critica_lectura", nullable = false)
    private CriticaLectura criticaLectura;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_id_cliente", referencedColumnName = "id_cliente", nullable = false)
    private Cliente cliente;
}
