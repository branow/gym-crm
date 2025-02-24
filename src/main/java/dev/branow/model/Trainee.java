package dev.branow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "trainees")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
public class Trainee extends User {

    LocalDate dateOfBirth;

    String address;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL)
    List<Training> trainings;

    @ManyToMany
    @JoinTable(
            name = "trainee_favorite_trainers",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    List<Trainer> favoriteTrainers;

}
