package dev.branow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "trainers")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
public class Trainer extends User {
    @ManyToOne @JoinColumn(name = "specialization", referencedColumnName = "id")
    TrainingType specialization;
    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    List<Training> trainings;
}
