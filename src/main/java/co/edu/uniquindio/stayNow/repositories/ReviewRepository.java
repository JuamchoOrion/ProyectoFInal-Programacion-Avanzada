package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
}
