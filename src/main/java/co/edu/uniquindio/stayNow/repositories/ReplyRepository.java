package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // Check if a reply already exists for a given review
    boolean existsByReview_Id(Long reviewId);

    // Retrieve the reply associated with a given review
    Optional<Reply> findByReview_Id(Long reviewId);
}
