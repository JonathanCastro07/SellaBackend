package com.JonathanCastro07.Sella.DTOs;

import com.JonathanCastro07.Sella.modelo.EstadoRifa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RifaOrganizadorResponse(
        Long id,
        String nombre,
        Integer digitos,
        BigDecimal precio,
        String loteriaRef,
        LocalDate fechaSorteo,
        EstadoRifa estado,
        String numeroGanador,
        List<NumeroOrganizadorResponse> numeros
) {}
