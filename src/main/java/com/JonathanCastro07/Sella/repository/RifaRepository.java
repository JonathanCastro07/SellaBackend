package com.JonathanCastro07.Sella.repository;

import com.JonathanCastro07.Sella.modelo.EstadoRifa;
import com.JonathanCastro07.Sella.modelo.Rifa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RifaRepository extends JpaRepository<Rifa, Long> {
    List<Rifa> findByOrganizador_Id(Long organizadorId);
    List<Rifa> findByEstadoAndFechaSorteoBefore(EstadoRifa estado, LocalDate fecha);
}
