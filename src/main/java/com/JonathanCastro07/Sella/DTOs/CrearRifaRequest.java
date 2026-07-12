package com.JonathanCastro07.Sella.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearRifaRequest(
        @NotBlank String nombre,
        @Min(2) @Max(3) Integer digitos,
        @DecimalMin("0.0") BigDecimal precio,
        String loteriaRef,
        LocalDate fechaSorteo,
        @NotBlank String cuentaPago
) {}
