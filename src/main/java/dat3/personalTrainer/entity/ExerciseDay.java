package dat3.personalTrainer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ExerciseDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_plan_id")
    private ExercisePlan exercisePlan;

    private String name;

    @OneToMany(mappedBy = "exerciseDay", cascade = CascadeType.ALL)
    private List<Exercise> exercises;

    public ExerciseDay(ExercisePlan exercisePlan, String name, List<Exercise> exercises) {
        this.exercisePlan = exercisePlan;
        this.name = name;
        this.exercises = exercises;
    }
}