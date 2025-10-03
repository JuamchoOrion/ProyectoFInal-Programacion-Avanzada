package co.edu.uniquindio.stayNow.dto;

public record EmailDTO(
        String subject,
        String body,
        String recipient
) {
}