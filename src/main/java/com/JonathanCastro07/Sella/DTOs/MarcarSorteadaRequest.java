package com.JonathanCastro07.Sella.DTOs;

import com.JonathanCastro07.Sella.modelo.EstadoRifa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MarcarSorteadaRequest(
        Long id,
        String nombre,
        Integer digitos,
        BigDecimal precio,
        String loteriaRef,
        LocalDate fechaSorteo,
        String cuentaPago,
        EstadoRifa estado,
        String numeroGanador,
        List<NumeroResponse> numeros
) {}
