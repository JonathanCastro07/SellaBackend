package com.JonathanCastro07.Sella.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String remitente;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL_BREVO = "https://api.brevo.com/v3/smtp/email";

    public void enviarConfirmacionPago(String destinatario, String nombreComprador,
                                       String rifaNombre, String numero, LocalDate fechaSorteo) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            Map<String, Object> sender = Map.of(
                    "name", "Sella",
                    "email", remitente
            );

            Map<String, Object> destinatarioMap = Map.of(
                    "email", destinatario,
                    "name", nombreComprador
            );

            String fechaTexto = fechaSorteo != null
                    ? fechaSorteo.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES")))
                    : "por confirmar";

            String texto =
                    "Hola " + nombreComprador + ",\n\n" +
                            "Tu pago del número " + numero + " en la rifa \"" + rifaNombre + "\" " +
                            "fue confirmado por el organizador.\n\n" +
                            "Fecha del sorteo: " + fechaTexto + "\n\n" +
                            "¡Mucha suerte!\n\n" +
                            "— Sella";

            Map<String, Object> body = Map.of(
                    "sender", sender,
                    "to", List.of(destinatarioMap),
                    "subject", "¡Tu pago fue confirmado! - " + rifaNombre,
                    "textContent", texto
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(URL_BREVO, request, String.class);

        } catch (Exception e) {
            System.err.println("Error enviando correo de confirmación: " + e.getMessage());
        }
    }
}