package co.edu.uniquindio.stayNow.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private Reply reply;
    @ManyToOne
    private Accommodation accommodation;
    @OneToOne
    private Reservation reservation;
    @Size(max = 500)
    private String comment;
    @Min(1)
    @Max(5)
    private Integer rating;

    private int rate;
    private LocalDateTime createdAt;
}
