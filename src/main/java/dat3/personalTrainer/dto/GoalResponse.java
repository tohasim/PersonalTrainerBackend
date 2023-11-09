package dat3.personalTrainer.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import dat3.personalTrainer.entity.Goal;
import dat3.personalTrainer.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalResponse {
    String name;
    String description;
    List<User> users;

    public GoalResponse(Goal goal) {
        name = goal.getName();
        description = goal.getDescription();
        users = goal.getUsers();
    }
}
