package com.JonathanCastro07.Sella.repository;

import com.JonathanCastro07.Sella.modelo.EstadoNumero;
import com.JonathanCastro07.Sella.modelo.Numero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NumeroRepository extends JpaRepository<Numero,Long> {
    List<Numero> findByEstadoAndDeadlineBefore(EstadoNumero estado, LocalDateTime fecha);
    Optional<Numero> findByRifa_IdAndNumero(Long rifaId, String numero);
}
