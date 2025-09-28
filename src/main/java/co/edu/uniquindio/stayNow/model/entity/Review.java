package co.edu.uniquindio.stayNow.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    private long id;
    @ManyToOne
    private User user;
    @OneToOne
    private Reply reply;
    @ManyToOne
    private Accommodation accommodation;
    private String comment;

    private LocalDateTime createdAt;
}
