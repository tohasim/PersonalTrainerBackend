package dat3.personalTrainer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ExercisePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonBackReference
    private User user;

    private String name;

    @OneToMany(mappedBy = "exercisePlan", cascade = CascadeType.ALL)
    private List<ExerciseDay> exerciseDays;

    public ExercisePlan(User user, String name, List<ExerciseDay> exerciseDays) {
        this.user = user;
        this.name = name;
        this.exerciseDays = exerciseDays;
    }
}