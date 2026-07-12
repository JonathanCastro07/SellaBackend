package com.JonathanCastro07.Sella.service;

import com.JonathanCastro07.Sella.modelo.EstadoNumero;
import com.JonathanCastro07.Sella.modelo.Numero;
import com.JonathanCastro07.Sella.repository.NumeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TelegramUpdatesService {

    private final TelegramService telegramService;
    private final NumeroRepository numeroRepository;
    @Autowired
    private EmailService emailService;

    private long ultimoOffset = 0;

    public TelegramUpdatesService(TelegramService telegramService, NumeroRepository numeroRepository) {
        this.telegramService = telegramService;
        this.numeroRepository = numeroRepository;
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void procesarActualizaciones() {
        System.out.println("Revisando Telegram... offset actual: " + ultimoOffset);

        Map<String, Object> respuesta = telegramService.obtenerActualizaciones(ultimoOffset);

        Object resultObj = respuesta.get("result");
        if (!(resultObj instanceof List<?> updates) || updates.isEmpty()) {
            return;
        }

        for (Object updateObj : updates) {
            if (!(updateObj instanceof Map<?, ?> update)) continue;

            Object updateId = update.get("update_id");
            if (updateId instanceof Number n) {
                ultimoOffset = n.longValue() + 1;
            }

            Object callbackObj = update.get("callback_query");
            if (callbackObj instanceof Map<?, ?> callback) {
                procesarCallback(callback);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void procesarCallback(Map<?, ?> callback) {
        String callbackId = (String) callback.get("id");
        String data = (String) callback.get("data");

        Map<String, Object> mensaje = (Map<String, Object>) callback.get("message");
        Map<String, Object> chat = (Map<String, Object>) mensaje.get("chat");
        Long chatId = ((Number) chat.get("id")).longValue();
        Integer messageId = (Integer) mensaje.get("message_id");

        if (data == null) return;
        String[] partes = data.split(":");
        if (partes.length != 3) return;

        String accion = partes[0];
        Long rifaId = Long.parseLong(partes[1]);
        String numero = partes[2];

        Optional<Numero> numeroOpt = numeroRepository.findByRifa_IdAndNumero(rifaId, numero);

        if (numeroOpt.isEmpty()) {
            telegramService.responderCallback(callbackId, "Número no encontrado");
            return;
        }

        Numero n = numeroOpt.get();

        if (n.getEstado() != EstadoNumero.PENDIENTE) {
            telegramService.responderCallback(callbackId, "Este número ya fue procesado antes");
            telegramService.editarMensaje(chatId, messageId,
                    "ℹ️ Número " + numero + " ya estaba en estado: " + n.getEstado());
            return;
        }

        if (accion.equals("confirmar")) {
            n.setEstado(EstadoNumero.PAGADO);
            n.setDeadline(null);
            numeroRepository.save(n);

            emailService.enviarConfirmacionPago(
                    n.getComprador().getEmail(),
                    n.getComprador().getNombre(),
                    n.getRifa().getNombre(),
                    numero
            );

            telegramService.responderCallback(callbackId, "Pago confirmado ✅");
            telegramService.editarMensaje(chatId, messageId,
                    "✅ *Pago confirmado*\nNúmero " + numero + " marcado como PAGADO.");

        } else if (accion.equals("rechazar")) {
            n.setEstado(EstadoNumero.DISPONIBLE);
            n.setDeadline(null);
            n.setComprador(null);
            numeroRepository.save(n);

            telegramService.responderCallback(callbackId, "Apartado rechazado ❌");
            telegramService.editarMensaje(chatId, messageId,
                    "❌ *Apartado rechazado*\nNúmero " + numero + " liberado de nuevo.");
        }
    }
}
