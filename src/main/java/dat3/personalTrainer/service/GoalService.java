package dat3.personalTrainer.service;

import dat3.personalTrainer.dto.GoalResponse;
import dat3.personalTrainer.entity.Goal;
import dat3.personalTrainer.repository.GoalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GoalService {
    GoalRepository repository;

    public GoalService(GoalRepository repository) {
        this.repository = repository;
    }

    public GoalResponse getGoal(int id){
        Goal goal = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Goal with this id does not exist"));
        return new GoalResponse(goal);
    }
}
