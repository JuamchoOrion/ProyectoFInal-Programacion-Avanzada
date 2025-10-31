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
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "The requested resource does not exist."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> generalExceptionHandler(Exception e){
        return ResponseEntity.internalServerError().body(new ResponseDTO<>(true, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<List<ValidationDTO>>> validationExceptionHandler(MethodArgumentNotValidException ex) {
        List<ValidationDTO> errors = new ArrayList<>();
        BindingResult results = ex.getBindingResult();
        for (FieldError e : results.getFieldErrors()) {
            errors.add(new ValidationDTO(e.getField(), e.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(new ResponseDTO<>(true, errors));
    }

    // ============================================================
    // Authentication / Users
    // ============================================================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> userNotFoundExceptionHandler(UserNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "The user does not exist."));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDTO<String>> invalidCredentialsExceptionHandler(InvalidCredentialsException ex){
        return ResponseEntity.status(401).body(new ResponseDTO<>(true, "Invalid credentials."));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO<String>> emailAlreadyExistsExceptionHandler(EmailAlreadyExistsException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "This email is already registered."));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ResponseDTO<String>> unauthorizedActionExceptionHandler(UnauthorizedActionException ex){
        return ResponseEntity.status(403).body(new ResponseDTO<>(true, "Action not allowed."));
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ResponseDTO<String>> accountDisabledExceptionHandler(AccountDisabledException ex){
        return ResponseEntity.status(403).body(new ResponseDTO<>(true, "This account is suspended or inactive."));
    }

    // 🔹 NUEVO: Código de restablecimiento inválido
    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<ResponseDTO<String>> invalidResetCodeExceptionHandler(InvalidResetCodeException ex){
        return ResponseEntity.status(400).body(new ResponseDTO<>(true, "The reset code is invalid."));
    }

    // 🔹 NUEVO: Código de restablecimiento expirado
    @ExceptionHandler(ExpiredResetCodeException.class)
    public ResponseEntity<ResponseDTO<String>> expiredResetCodeExceptionHandler(ExpiredResetCodeException ex){
        return ResponseEntity.status(410).body(new ResponseDTO<>(true, "The reset code has expired."));
    }

    @ExceptionHandler(PasswordResetCodeNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> passwordResetCodeNotAllowedExceptionHandler(PasswordResetCodeNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "Password reset code not found."));
    }

    // 🔹 CORREGIDO: Contraseña actual no coincide
    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResponseDTO<String>> passwordNotMatchException(PasswordNotMatchException ex){
        return ResponseEntity.status(400).body(new ResponseDTO<>(true, "The current password does not match."));
    }

    // ============================================================
    // Accommodations
    // ============================================================
    @ExceptionHandler(AccommodationNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> accommodationNotFoundExceptionHandler(AccommodationNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "The accommodation does not exist."));
    }

    @ExceptionHandler(AccommodationUnavailableException.class)
    public ResponseEntity<ResponseDTO<String>> accommodationUnavailableExceptionHandler(AccommodationUnavailableException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "The accommodation is not available. It may be occupied."));
    }

    @ExceptionHandler(InvalidAccommodationDataException.class)
    public ResponseEntity<ResponseDTO<String>> invalidAccommodationDataException(InvalidAccommodationDataException ex){
        return ResponseEntity.status(401).body(new ResponseDTO<>(true, "Invalid accommodation data."));
    }

    @ExceptionHandler(MaxGuestsExceededException.class)
    public ResponseEntity<ResponseDTO<String>> maxGuestsExceededException(MaxGuestsExceededException ex){
        return ResponseEntity.status(422).body(new ResponseDTO<>(true, "The number of guests exceeds the maximum allowed for this accommodation."));
    }

    // ============================================================
    // Reservations
    // ============================================================
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> reservationNotFoundExceptionHandler(ReservationNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "The reservation does not exist."));
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ResponseDTO<String>> reservationConflictExceptionHandler(ReservationConflictException ex){
        return ResponseEntity.status(422).body(new ResponseDTO<>(true, "There is already a reservation for those dates. Please try again with different dates."));
    }

    @ExceptionHandler(InvalidReservationDateException.class)
    public ResponseEntity<ResponseDTO<String>> invalidReservationDateException(InvalidReservationDateException ex){
        return ResponseEntity.status(422).body(new ResponseDTO<>(true, "Invalid reservation dates. Please check the order."));
    }

    @ExceptionHandler(ReservationCancellationNotAllowedException.class)
    public ResponseEntity<ResponseDTO<String>> reservationCancellationNotAllowedException(ReservationCancellationNotAllowedException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "Reservation cannot be canceled according to the policy."));
    }

    // ============================================================
    // Reviews
    // ============================================================
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> reviewNotFoundExceptionHandler(ReviewNotFoundException ex){
        return ResponseEntity.status(404).body(new ResponseDTO<>(true, "The review does not exist."));
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ResponseDTO<String>> duplicateReviewExceptionHandler(DuplicateReviewException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "You have already submitted a review for this reservation."));
    }

    @ExceptionHandler(UnauthorizedReviewException.class)
    public ResponseEntity<ResponseDTO<String>> unauthorizedReviewExceptionHandler(UnauthorizedReviewException ex){
        return ResponseEntity.status(403).body(new ResponseDTO<>(true, "You are not authorized to leave a review for this accommodation. You may not have made a reservation."));
    }

    @ExceptionHandler(ReplyAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO<String>> replyAlreadyExistsExceptionHandler(ReplyAlreadyExistsException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "This review already has a reply."));
    }

    // ============================================================
    // General
    // ============================================================
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO<String>> badRequestExceptionHandler(BadRequestException ex){
        return ResponseEntity.status(400).body(new ResponseDTO<>(true, "The request contains invalid data."));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ResponseDTO<String>> resourceConflictExceptionHandler(ResourceConflictException ex){
        return ResponseEntity.status(409).body(new ResponseDTO<>(true, "Conflict with the current state of the resource."));
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<ResponseDTO<String>> operationNotAllowedExceptionHandler(OperationNotAllowedException ex){
        return ResponseEntity.status(403).body(new ResponseDTO<>(true, "Operation not allowed according to business rules."));
    }
}