package dat3.personalTrainer.dto;

import dat3.personalTrainer.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    String username, email, password, gender;
    int age;
    double weightInKg;

    public static User getUserEntity(UserRequest request){
        return new User(
                request.username,
                request.email,
                request.password,
                request.age,
                request.weightInKg,
                request.gender);
    }
}
