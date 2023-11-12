package dat3.personalTrainer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dat3.security.entity.UserWithRoles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE")
public class User extends UserWithRoles {
    int age;
    double weightInKg;
    String gender;

    @OneToOne
    @JsonBackReference
    private ExercisePlan exercisePlan;


    @ManyToOne
            @JsonBackReference
    Goal goal;

    public User(String username, String email, String password, int age, double weightInKg, String gender, Goal goal) {
        super(username, email, password);
        this.age = age;
        this.weightInKg = weightInKg;
        this.gender = gender;
        this.goal = goal;
    }

}
