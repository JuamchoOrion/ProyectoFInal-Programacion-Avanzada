package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import co.edu.uniquindio.stayNow.model.enums.AccommodationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 *
 *
 * **/
import java.util.List;

// dentro de el generico va la entidad y el tipo de id
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findAllByAccommodationStatus(AccommodationStatus status);
}
