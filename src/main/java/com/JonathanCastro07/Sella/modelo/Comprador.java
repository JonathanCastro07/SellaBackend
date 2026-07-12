package com.JonathanCastro07.Sella.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comprador ")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comprador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    private String celular;

    @Column(nullable = false)
    @Email
    private String email;

    private String urlComprobante;


}
