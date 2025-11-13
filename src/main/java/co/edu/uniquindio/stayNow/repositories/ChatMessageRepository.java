package co.edu.uniquindio.stayNow.repositories;

import co.edu.uniquindio.stayNow.model.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            String senderId1, String receiverId1,
            String senderId2, String receiverId2
    );
    @Query("""
    SELECT DISTINCT\s
        CASE\s
            WHEN m.senderId = :userId THEN m.receiverId\s
            ELSE m.senderId\s
        END
    FROM ChatMessage m
    WHERE (m.senderId = :userId OR m.receiverId = :userId)
      AND (CASE\s
              WHEN m.senderId = :userId THEN m.receiverId\s
              ELSE m.senderId\s
           END) <> :userId
""")
    List<String> findDistinctContacts(String userId);

}
