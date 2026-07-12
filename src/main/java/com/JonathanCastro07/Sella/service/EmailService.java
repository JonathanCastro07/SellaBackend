package com.JonathanCastro07.Sella.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarConfirmacionPago(String destinatario, String nombreComprador,
                                       String rifaNombre, String numero) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(destinatario);
            mensaje.setSubject("¡Tu pago fue confirmado! - " + rifaNombre);
            mensaje.setText(
                    "Hola " + nombreComprador + ",\n\n" +
                            "Tu pago del número " + numero + " en la rifa \"" + rifaNombre + "\" " +
                            "fue confirmado por el organizador.\n\n" +
                            "¡Mucha suerte en el sorteo!\n\n" +
                            "— Sella"
            );
            mailSender.send(mensaje);
        } catch (Exception e) {
            // Un fallo al enviar el correo no debe romper el flujo de confirmar el pago
            System.err.println("Error enviando correo de confirmación: " + e.getMessage());
        }
    }
}