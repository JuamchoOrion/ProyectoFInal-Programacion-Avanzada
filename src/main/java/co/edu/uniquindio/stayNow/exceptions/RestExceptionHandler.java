package co.edu.uniquindio.stayNow.exceptions;
import co.edu.uniquindio.stayNow.dto.ResponseDTO;
import co.edu.uniquindio.stayNow.dto.ValidationDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDTO<String>> noResourceFoundExceptionHandler(NoResourceFoundException ex){
        return ResponseEntity.status(404).body( new ResponseDTO<>(true, "El recurso solicitado no existe"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> generalExceptionHandler (Exception e){
        return ResponseEntity.internalServerError().body( new ResponseDTO<>(true, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<List<ValidationDTO>>> validationExceptionHandler ( MethodArgumentNotValidException ex ) {
        List<ValidationDTO> errors = new ArrayList<>();
        BindingResult results = ex.getBindingResult();
        for (FieldError e: results.getFieldErrors()) {
            errors.add( new ValidationDTO(e.getField(), e.getDefaultMessage()) );
        }
        return ResponseEntity.badRequest().body( new ResponseDTO<>(true, errors));
    }

    // Autenticación / Usuarios
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> userNotFoundExceptionHandler(UserNotFoundException ex){
        return ResponseEntity.status(404).body( new ResponseDTO<>(true, "El Usuario no existe."));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDTO<String>> invalidCredentialsExceptionHandler(InvalidCredentialsException ex){
        return ResponseEntity.status(401).body( new ResponseDTO<>(true, "Credenciales inválidas."));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO<String>> emailAlreadyExistsExceptionHandler(EmailAlreadyExistsException ex){
        return ResponseEntity.status(409).body( new ResponseDTO<>(true, "Este email ya está registrado."));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ResponseDTO<String>> unauthorizedActionExceptionHandler(UnauthorizedActionException ex){
        return ResponseEntity.status(403).body( new ResponseDTO<>(true, "Accion no permitida."));
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ResponseDTO<String>> accountDisabledExceptionHandler(AccountDisabledException ex){
        return ResponseEntity.status(403).body( new ResponseDTO<>(true, "Esta cuenta está suspendida o inactiva."));
    }

    // Alojamientos
    @ExceptionHandler(AccommodationNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> accommodationNotFoundExceptionHandler(AccommodationNotFoundException ex){
        return ResponseEntity.status(404).body( new ResponseDTO<>(true, "El Alojamiento no existe."));
    }

    @ExceptionHandler(AccommodationUnavailableException.class)
    public ResponseEntity<ResponseDTO<String>> accommodationUnavailableExceptionHandler(AccommodationUnavailableException ex){
        return ResponseEntity.status(409).body( new ResponseDTO<>(true, "El alojamiento no está disponible, es posible que esté ocupado."));
    }

    @ExceptionHandler(InvalidAccommodationDataException.class)
    public ResponseEntity<ResponseDTO<String>> invalidAccommodationDataException(InvalidAccommodationDataException ex){
        return ResponseEntity.status(401).body( new ResponseDTO<>(true, "Datos inválidos para el alojamiento."));
    }

    @ExceptionHandler(MaxGuestsExceededException.class)
    public ResponseEntity<ResponseDTO<String>> maxGuestsExceededException(MaxGuestsExceededException ex){
        return ResponseEntity.status(422).body( new ResponseDTO<>(true, "La cantidad de huéspedes excede el máximo permitido para el alojamiento."));
    }

    // Reservas
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> reservationNotFoundExceptionHandler(ReservationNotFoundException ex){
        return ResponseEntity.status(404).body( new ResponseDTO<>(true, "La reserva no existe."));
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ResponseDTO<String>> reservationConflictExceptionHandler(ReservationConflictException ex){
        return ResponseEntity.status(422).body( new ResponseDTO<>(true, "Ya hay una reserva en esas fechas. Intente nuevamente con fechas distintas."));
    }

    @ExceptionHandler(InvalidReservationDateException.class)
    public ResponseEntity<ResponseDTO<String>> invalidReservationDateException(InvalidReservationDateException ex){
        return ResponseEntity.status(422).body( new ResponseDTO<>(true, "Fechas inválidas en la reserva. Revise el orden."));
    }

    @ExceptionHandler(ReservationCancellationNotAllowedException.class)
    public ResponseEntity<ResponseDTO<String>> reservationCancellationNotAllowedException(ReservationCancellationNotAllowedException ex){
        return ResponseEntity.status(409).body( new ResponseDTO<>(true, "No se puede cancelar la reserva, según la política."));
    }

    // Reseñas
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> reviewNotFoundExceptionHandler(ReviewNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "La reseña no existe."));
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ResponseDTO<String>> duplicateReviewExceptionHandler(DuplicateReviewException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "Usted ya realizó una reseña para esta reserva."));
    }

    @ExceptionHandler(UnauthorizedReviewException.class)
    public ResponseEntity<ResponseDTO<String>> unauthorizedReviewExceptionHandler(UnauthorizedReviewException ex){
        return ResponseEntity.status(403).body(new ResponseDTO<>(true, "No tiene permisos para dejar una reseña en este alojamiento. Es posible que no haya hecho una reserva."));
    }

    @ExceptionHandler(ReplyAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO<String>> replyAlreadyExistsExceptionHandler(ReplyAlreadyExistsException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "Este comentario ya tiene una respuesta."));
    }

    // Generales
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO<String>> badRequestExceptionHandler(BadRequestException ex){
        return ResponseEntity.status(400).body(new ResponseDTO<>(true, "La petición contiene datos inválidos."));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ResponseDTO<String>> resourceConflictExceptionHandler(ResourceConflictException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "Conflicto con el estado actual del recurso."));
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<ResponseDTO<String>> operationNotAllowedExceptionHandler(OperationNotAllowedException ex){
        return ResponseEntity.status(403).body(new ResponseDTO<>(true, "La operación no está permitida por las reglas de negocio."));
    }
    @ExceptionHandler(PasswordResetCodeNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> passwordResetCodeNotAllowedExceptionHandler(PasswordResetCodeNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "PasswordResetCode not found"));
    }

}