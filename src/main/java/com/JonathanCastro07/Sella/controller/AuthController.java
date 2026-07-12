package com.JonathanCastro07.Sella.controller;

import com.JonathanCastro07.Sella.DTOs.AuthResponse;
import com.JonathanCastro07.Sella.DTOs.LoginRequest;
import com.JonathanCastro07.Sella.DTOs.RegisterRequest;
import com.JonathanCastro07.Sella.modelo.Usuario;
import com.JonathanCastro07.Sella.repository.UsuarioRepository;
import com.JonathanCastro07.Sella.security.Jwtservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private Jwtservice jwtservice;

    @PostMapping("/register")
    public ResponseEntity<?> registar(@RequestBody RegisterRequest request){
        if (usuarioRepository.findByEmail(request.email()).isPresent()){
            return ResponseEntity.badRequest().body("El email ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );

        UserDetails userDetails = usuarioRepository.findByEmail(request.email())
                .map(usuario -> org.springframework.security.core.userdetails.User.builder()
                        .username(usuario.getEmail())
                        .password(usuario.getPasswordHash())
                        .authorities("ORGANIZADOR")
                        .build())
                .orElseThrow();

        String token = jwtservice.generarToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));

    }

    @GetMapping("/perfil")
    public String perfil(Authentication authentication) {
        return "Hola, " + authentication.getName() + ". Accediste con éxito a un endpoint protegido.";
    }
}



