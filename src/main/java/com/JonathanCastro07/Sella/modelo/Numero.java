package com.JonathanCastro07.Sella.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(
        name = "numeros",
        uniqueConstraints = @UniqueConstraint(columnNames = {"rifa_id", "numero"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Numero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNumero estado = EstadoNumero.DISPONIBLE;

    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rifa_id", nullable = false)
    private Rifa rifa;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comprador_id")
    private Comprador comprador;
}
