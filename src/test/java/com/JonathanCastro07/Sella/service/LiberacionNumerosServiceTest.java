package com.JonathanCastro07.Sella.service;


import com.JonathanCastro07.Sella.modelo.*;
import com.JonathanCastro07.Sella.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(LiberacionNumerosService.class)
class LiberacionNumerosServiceTest {

    @Autowired private RifaRepository rifaRepository;
    @Autowired private NumeroRepository numeroRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private LiberacionNumerosService liberacionNumerosService;

    @Test
    void liberaNumeroConDeadlineVencido() {
        Usuario organizador = new Usuario();
        organizador.setNombre("Jonathan");
        organizador.setEmail("test-liberacion@sella.com");
        organizador.setPasswordHash("hash-falso");
        usuarioRepository.save(organizador);

        Rifa rifa = new Rifa();
        rifa.setNombre("Rifa de prueba - liberación");
        rifa.setDigitos(2);
        rifa.setPrecio(new BigDecimal("15000"));
        rifa.setCuentaPago("Nequi 3000000000");
        rifa.setOrganizador(organizador);
        rifaRepository.save(rifa);

        Comprador comprador = new Comprador();
        comprador.setNombre("Ana");
        comprador.setCelular("3001234567");

        Numero numero = new Numero();
        numero.setNumero("07");
        numero.setRifa(rifa);
        numero.setEstado(EstadoNumero.PENDIENTE);
        numero.setDeadline(LocalDateTime.now().minusMinutes(5)); // ya vencido
        numero.setComprador(comprador);
        numeroRepository.save(numero);


        liberacionNumerosService.liberarNumerosVencidos();

        Numero actualizado = numeroRepository.findById(numero.getId()).orElseThrow();
        assertThat(actualizado.getEstado()).isEqualTo(EstadoNumero.DISPONIBLE);
        assertThat(actualizado.getDeadline()).isNull();
        assertThat(actualizado.getComprador()).isNull();
    }
}
