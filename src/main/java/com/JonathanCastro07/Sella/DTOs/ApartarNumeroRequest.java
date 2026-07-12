package com.JonathanCastro07.Sella.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ApartarNumeroRequest(
        @NotBlank String nombre,
        @NotBlank String celular,
        @NotBlank @Email String email
) {}
