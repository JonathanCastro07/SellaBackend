package com.JonathanCastro07.Sella.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    private String urlBase() {
        return "https://api.telegram.org/bot" + botToken;
    }


    public void enviarMensaje(String texto) {
        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", texto,
                "parse_mode", "Markdown"
        );
        post("/sendMessage", body);
    }


    public void enviarMensajeConBotones(String texto, Long rifaId, String numero) {
        Map<String, Object> botonConfirmar = Map.of(
                "text", "✅ Confirmar",
                "callback_data", "confirmar:" + rifaId + ":" + numero
        );
        Map<String, Object> botonRechazar = Map.of(
                "text", "❌ Rechazar",
                "callback_data", "rechazar:" + rifaId + ":" + numero
        );

        Map<String, Object> teclado = Map.of(
                "inline_keyboard", List.of(List.of(botonConfirmar, botonRechazar))
        );

        Map<String, Object> body = Map.of(
                "chat_id", chatId,
                "text", texto,
                "parse_mode", "Markdown",
                "reply_markup", teclado
        );
        post("/sendMessage", body);
    }

    public void responderCallback(String callbackQueryId, String mensajeCorto) {
        Map<String, Object> body = Map.of(
                "callback_query_id", callbackQueryId,
                "text", mensajeCorto
        );
        post("/answerCallbackQuery", body);
    }


    public void editarMensaje(Long chatIdMensaje, Integer messageId, String nuevoTexto) {
        Map<String, Object> body = Map.of(
                "chat_id", chatIdMensaje,
                "message_id", messageId,
                "text", nuevoTexto,
                "parse_mode", "Markdown"
        );
        post("/editMessageText", body);
    }

    public Map<String, Object> obtenerActualizaciones(long offset) {
        String url = urlBase() + "/getUpdates?offset=" + offset + "&timeout=0";
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("Error consultando updates de Telegram: " + e.getMessage());
            return Map.of("ok", false, "result", List.of());
        }
    }

    private void post(String endpoint, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForEntity(urlBase() + endpoint, request, String.class);
        } catch (Exception e) {
            System.err.println("Error llamando a Telegram (" + endpoint + "): " + e.getMessage());
        }
    }
}