package com.JonathanCastro07.Sella.controller;

import com.JonathanCastro07.Sella.DTOs.*;
import com.JonathanCastro07.Sella.Exception.AccesoDenegadoException;
import com.JonathanCastro07.Sella.Exception.EstadoInvalidoException;
import com.JonathanCastro07.Sella.Exception.RecursoNoEncontradoException;
import com.JonathanCastro07.Sella.modelo.*;
import com.JonathanCastro07.Sella.repository.NumeroRepository;
import com.JonathanCastro07.Sella.repository.RifaRepository;
import com.JonathanCastro07.Sella.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rifas")
public class RifaController {

    @Autowired
    private RifaRepository rifaRepository;
    @Autowired
    private NumeroRepository numeroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;


    @PostMapping
    public ResponseEntity<MarcarSorteadaRequest> crear(@Valid @RequestBody CrearRifaRequest request,
                                                       Authentication authentication) {

        Usuario organizador = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Organizador no encontrado"));

        Rifa rifa = new Rifa();
        rifa.setNombre(request.nombre());
        rifa.setDigitos(request.digitos());
        rifa.setPrecio(request.precio());
        rifa.setLoteriaRef(request.loteriaRef());
        rifa.setFechaSorteo(request.fechaSorteo());
        rifa.setCuentaPago(request.cuentaPago());
        rifa.setOrganizador(organizador);
        rifaRepository.save(rifa);

        int totalNumeros = (int) Math.pow(10, request.digitos());
        List<Numero> numeros = new ArrayList<>();
        for (int i = 0; i < totalNumeros; i++) {
            Numero numero = new Numero();
            numero.setNumero(String.format("%0" + request.digitos() + "d", i));
            numero.setEstado(EstadoNumero.DISPONIBLE);
            numero.setRifa(rifa);
            numeros.add(numero);
        }
        numeroRepository.saveAll(numeros);
        rifa.setNumeros(numeros);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(rifa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcarSorteadaRequest> verTablero(@PathVariable Long id) {
        Rifa rifa = rifaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rifa no encontrada"));
        return ResponseEntity.ok(toResponse(rifa));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<MarcarSorteadaRequest>> misRifas(Authentication authentication) {
        Usuario organizador = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Organizador no encontrado"));

        List<Rifa> rifas = rifaRepository.findByOrganizador_Id(organizador.getId());
        List<MarcarSorteadaRequest> response = rifas.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/organizador")
    public ResponseEntity<RifaOrganizadorResponse> verComoOrganizador(@PathVariable Long id,
                                                                      Authentication authentication) {

        Rifa rifa = rifaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rifa no encontrada"));

        Usuario organizador = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Organizador no encontrado"));

        if (!rifa.getOrganizador().getId().equals(organizador.getId())) {
            throw new AccesoDenegadoException("No tienes permiso sobre esta rifa");
        }

        List<NumeroOrganizadorResponse> numeros = rifa.getNumeros().stream()
                .map(n -> new NumeroOrganizadorResponse(
                        n.getNumero(),
                        n.getEstado(),
                        n.getComprador() != null
                                ? new CompradorResponse(n.getComprador().getNombre(), n.getComprador().getCelular(), n.getComprador().getUrlComprobante())
                                : null
                ))
                .toList();

        RifaOrganizadorResponse response = new RifaOrganizadorResponse(
                rifa.getId(), rifa.getNombre(), rifa.getDigitos(), rifa.getPrecio(),
                rifa.getLoteriaRef(), rifa.getFechaSorteo(), rifa.getEstado(), rifa.getNumeroGanador(), numeros
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/marcar-sorteada")
    public ResponseEntity<?> marcarSorteada(@PathVariable Long id,
                                            @Valid @RequestBody MarcarSorteadaRequest request,
                                            Authentication authentication) {

        Rifa rifa = rifaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rifa no encontrada"));

        Usuario organizador = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Organizador no encontrado"));

        if (!rifa.getOrganizador().getId().equals(organizador.getId())) {
            throw new AccesoDenegadoException("No tienes permiso sobre esta rifa");
        }

        if (rifa.getEstado() != EstadoRifa.CERRADA) {
            throw new EstadoInvalidoException("Solo puedes marcar como sorteada una rifa que ya esté cerrada");
        }

        boolean numeroExiste = numeroRepository.findByRifa_IdAndNumero(id, request.numeroGanador()).isPresent();
        if (!numeroExiste) {
            throw new EstadoInvalidoException("Ese número no existe en esta rifa");
        }

        rifa.setNumeroGanador(request.numeroGanador());
        rifa.setEstado(EstadoRifa.SORTEADA);
        rifaRepository.save(rifa);

        return ResponseEntity.ok("Rifa marcada como sorteada con número ganador: " + request.numeroGanador());
    }

    private MarcarSorteadaRequest toResponse(Rifa rifa) {
        List<NumeroResponse> numeros = rifa.getNumeros().stream()
                .map(n -> new NumeroResponse(n.getNumero(), n.getEstado()))
                .toList();

        return new MarcarSorteadaRequest(
                rifa.getId(),
                rifa.getNombre(),
                rifa.getDigitos(),
                rifa.getPrecio(),
                rifa.getLoteriaRef(),
                rifa.getFechaSorteo(),
                rifa.getCuentaPago(),
                rifa.getEstado(),
                rifa.getNumeroGanador(),
                numeros
        );
    }
}
