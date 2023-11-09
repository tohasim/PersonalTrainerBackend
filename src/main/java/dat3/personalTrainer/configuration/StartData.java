package dat3.personalTrainer.configuration;

import dat3.personalTrainer.entity.Goal;
import dat3.personalTrainer.repository.GoalRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller

public class StartData implements ApplicationRunner {
    GoalRepository goalRepository;

    public StartData(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Goal gainWeight = new Goal("Gain weight", "Gain muscle mass, good for slim people who would like to get bigger");
        Goal loseWeight = new Goal("Lose weight", "Lose fat, good for heavier people who would like to trim down");
        Goal bulk = new Goal("Bulk", "Add body mass, heavy on calories, and should be cut down later");
        Goal cut = new Goal("Cut", "Should be done after bulking, lose the extra body fat, but keep the muscle mass");
        goalRepository.saveAll(List.of(gainWeight, loseWeight, bulk, cut));
    }
}
