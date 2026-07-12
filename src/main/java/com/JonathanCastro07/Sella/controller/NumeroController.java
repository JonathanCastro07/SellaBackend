package com.JonathanCastro07.Sella.controller;

import com.JonathanCastro07.Sella.DTOs.ApartarNumeroRequest;
import com.JonathanCastro07.Sella.Exception.AccesoDenegadoException;
import com.JonathanCastro07.Sella.Exception.EstadoInvalidoException;
import com.JonathanCastro07.Sella.Exception.RecursoNoEncontradoException;
import com.JonathanCastro07.Sella.modelo.*;
import com.JonathanCastro07.Sella.repository.NumeroRepository;
import com.JonathanCastro07.Sella.repository.UsuarioRepository;
import com.JonathanCastro07.Sella.service.EmailService;
import com.JonathanCastro07.Sella.service.TelegramService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/rifas/{rifaId}/numeros/{numero}")
public class NumeroController {
    @Autowired
    private  NumeroRepository numeroRepository;
    @Autowired
    private  UsuarioRepository usuarioRepository;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private EmailService emailService;

    private static final long PLAZO_HORAS = 24;
    private static final String CARPETA_UPLOADS = "uploads/comprobantes";


    @PostMapping("/apartar")
    public ResponseEntity<?> apartar(@PathVariable Long rifaId,
                                     @PathVariable String numero,
                                     @Valid @RequestBody ApartarNumeroRequest request) {

        Numero n = numeroRepository.findByRifa_IdAndNumero(rifaId, numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Número no encontrado"));

        if (n.getEstado() != EstadoNumero.DISPONIBLE) {
            throw new EstadoInvalidoException("Este número ya no está disponible");
        }

        Comprador comprador = new Comprador();
        comprador.setNombre(request.nombre());
        comprador.setCelular(request.celular());
        comprador.setEmail(request.email());

        n.setComprador(comprador);
        n.setEstado(EstadoNumero.PENDIENTE);
        n.setDeadline(LocalDateTime.now().plusHours(PLAZO_HORAS));
        numeroRepository.save(n);

        telegramService.enviarMensajeConBotones(
                "🎟️ *Nuevo apartado*\n" +
                        "Rifa: " + n.getRifa().getNombre() + "\n" +
                        "Número: " + numero + "\n" +
                        "Comprador: " + request.nombre() + "\n" +
                        "Celular: " + request.celular(),
                rifaId,
                numero
        );

        return ResponseEntity.ok("Número apartado. Sube tu comprobante antes de que venza el plazo.");
    }

    @PostMapping("/comprobante")
    public ResponseEntity<?> subirComprobante(@PathVariable Long rifaId,
                                              @PathVariable String numero,
                                              @RequestParam("file") MultipartFile file) throws IOException {

        Numero n = numeroRepository.findByRifa_IdAndNumero(rifaId, numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Número no encontrado"));

        if (n.getEstado() != EstadoNumero.PENDIENTE || n.getComprador() == null) {
            throw new EstadoInvalidoException("Este numero ya no se encuentra disponible");
        }

        Path carpeta = Path.of(CARPETA_UPLOADS);
        Files.createDirectories(carpeta);

        String nombreArchivo = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path destino = carpeta.resolve(nombreArchivo);
        file.transferTo(destino);

        n.getComprador().setUrlComprobante(destino.toString());
        numeroRepository.save(n);

        return ResponseEntity.ok("Comprobante subido correctamente");
    }

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmar(@PathVariable Long rifaId,
                                       @PathVariable String numero,
                                       Authentication authentication) {

        Numero n = validarPermisoOrganizador(rifaId, numero, authentication);

        n.setEstado(EstadoNumero.PAGADO);
        n.setDeadline(null);
        numeroRepository.save(n);

        emailService.enviarConfirmacionPago(
                n.getComprador().getEmail(),
                n.getComprador().getNombre(),
                n.getRifa().getNombre(),
                numero
        );

        return ResponseEntity.ok("Pago confirmado");
    }

    @PostMapping("/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long rifaId,
                                      @PathVariable String numero,
                                      Authentication authentication) {

        Numero n = validarPermisoOrganizador(rifaId, numero, authentication);

        n.setEstado(EstadoNumero.DISPONIBLE);
        n.setDeadline(null);
        n.setComprador(null);
        numeroRepository.save(n);

        return ResponseEntity.ok("Apartado rechazado, número liberado");
    }



    private Numero validarPermisoOrganizador(Long rifaId, String numero, Authentication authentication) {
        Numero n = numeroRepository.findByRifa_IdAndNumero(rifaId, numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Número no encontrado"));

        Usuario organizador = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Organizador no encontrado"));

        if (!n.getRifa().getOrganizador().getId().equals(organizador.getId())) {
            throw new AccesoDenegadoException("No tienes permiso sobre esta rifa");
        }

        return n;
    }
}
