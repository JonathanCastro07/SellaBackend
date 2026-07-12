package com.JonathanCastro07.Sella.service;

import com.JonathanCastro07.Sella.modelo.Usuario;
import com.JonathanCastro07.Sella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontado: " + email));
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities("ORGANIZADOR")
                .build();
    }
}
