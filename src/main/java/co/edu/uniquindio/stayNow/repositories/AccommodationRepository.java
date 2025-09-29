package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
// dentro de el generico va la entidad y el tipo de id
public interface AccommodationRepository extends JpaRepository<Accommodation, String> {
}
