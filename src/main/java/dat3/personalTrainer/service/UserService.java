package dat3.personalTrainer.service;

import dat3.personalTrainer.dto.GoalResponse;
import dat3.personalTrainer.dto.UserRequest;
import dat3.personalTrainer.dto.UserResponse;
import dat3.personalTrainer.entity.Goal;
import dat3.personalTrainer.entity.User;
import dat3.personalTrainer.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    UserRepository repository;
    GoalService goalService;

    public UserService(UserRepository repository, GoalService goalService) {
        this.repository = repository;
        this.goalService = goalService;
    }

    public UserResponse getUser(String username){
        User user = repository.findById(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Member with this username does not exist"));
        return new UserResponse(user);
    }

    public GoalResponse getGoalsForUser(UserResponse userResponse){
        return goalService.getGoal(userResponse.getGoalId());
    }

    public UserResponse saveUser(UserRequest request) {
        Goal goal = goalService.getGoalEntity(request.getGoalId());
        User user = repository.save(UserRequest.getUserEntity(request, goal));
        return new UserResponse(user);
    }
}
