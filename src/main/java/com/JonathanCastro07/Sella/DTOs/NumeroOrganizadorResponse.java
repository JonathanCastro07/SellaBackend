package com.JonathanCastro07.Sella.DTOs;

import com.JonathanCastro07.Sella.modelo.EstadoNumero;

public record NumeroOrganizadorResponse(
        String numero,
        EstadoNumero estado,
        CompradorResponse comprador
) {}
