package com.JonathanCastro07.Sella.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombre;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "organizador", cascade = CascadeType.ALL)
    private List<Rifa> rifa = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaYhora = LocalDateTime.now();

}
