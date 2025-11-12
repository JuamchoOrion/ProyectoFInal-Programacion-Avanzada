package co.edu.uniquindio.stayNow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico para respuestas estándar del backend.
 *
 * @param <T> Tipo de dato que representa el contenido (content)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean error;
    private T content;
    private String message;

    public ResponseDTO(boolean error, T content) {
        this.error = error;
        this.content = content;
        this.message = null;
    }
}
