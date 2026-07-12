package com.JonathanCastro07.Sella.service;

import com.JonathanCastro07.Sella.modelo.EstadoNumero;
import com.JonathanCastro07.Sella.modelo.Numero;
import com.JonathanCastro07.Sella.repository.NumeroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LiberacionNumerosService {
    @Autowired
    private NumeroRepository numeroRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void liberarNumerosVencidos(){
        List<Numero> vencidos = numeroRepository
                .findByEstadoAndDeadlineBefore(EstadoNumero.PENDIENTE, LocalDateTime.now());

        for(Numero numero : vencidos){
            numero.setEstado(EstadoNumero.DISPONIBLE);
            numero.setDeadline(null);
            numero.setComprador(null);
        }
        numeroRepository.saveAll(vencidos);
    }


}
