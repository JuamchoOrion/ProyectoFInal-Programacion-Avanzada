package co.edu.uniquindio.stayNow.dto;

public record ResponseDTO<T>(
    boolean error,
    T content
) {
}
