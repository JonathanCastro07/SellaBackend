package com.JonathanCastro07.Sella.service;

import com.JonathanCastro07.Sella.modelo.EstadoRifa;
import com.JonathanCastro07.Sella.modelo.Rifa;
import com.JonathanCastro07.Sella.repository.RifaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CierreRifasService {

    private final RifaRepository rifaRepository;

    public CierreRifasService(RifaRepository rifaRepository) {
        this.rifaRepository = rifaRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cerrarRifasVencidas() {
        List<Rifa> vencidas = rifaRepository
                .findByEstadoAndFechaSorteoBefore(EstadoRifa.ACTIVA, LocalDate.now());

        for (Rifa rifa : vencidas) {
            rifa.setEstado(EstadoRifa.CERRADA);
        }

        rifaRepository.saveAll(vencidas);
    }
}
