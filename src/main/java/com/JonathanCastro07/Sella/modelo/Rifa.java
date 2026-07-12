package com.JonathanCastro07.Sella.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rifa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer digitos;

    @Column(nullable = false)
    private BigDecimal precio;

    private String loteriaRef;

    private LocalDate fechaSorteo;

    @Column(nullable = false)
    private String cuentaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRifa estado = EstadoRifa.ACTIVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario organizador;

    @OneToMany(mappedBy = "rifa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Numero> numeros = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaYhoraExa = LocalDateTime.now();

    private String numeroGanador;

}
