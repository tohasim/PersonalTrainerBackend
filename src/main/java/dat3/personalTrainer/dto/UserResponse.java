package dat3.personalTrainer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dat3.personalTrainer.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String username, email, gender;
    double weight;
    Integer age, goalId;

    public UserResponse(User user) {
        age = user.getAge();
        username = user.getUsername();
        email = user.getEmail();
        weight = user.getWeightInKg();
        gender = user.getGender();
        goalId = user.getGoal().getId();
    }
}
