package dat3.personalTrainer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_day_id")
    private ExerciseDay exerciseDay;

    private String name;
    private int sets;
    private int reps;

    public Exercise(ExerciseDay exerciseDay, String name, int sets, int reps) {
        this.exerciseDay = exerciseDay;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
    }
}
