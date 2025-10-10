package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // Saber si ya existe la respuesta en un review
    boolean existsByReview_Id(Long reviewId);

    // Obtener la reply de una review
    Optional<Reply> findByReview_Id(Long reviewId);
}
