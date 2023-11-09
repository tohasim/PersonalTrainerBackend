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
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String description;
    @OneToMany(mappedBy = "goal")
    List<User> users;

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
