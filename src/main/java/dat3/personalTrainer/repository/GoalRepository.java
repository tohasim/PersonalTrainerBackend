package dat3.personalTrainer.repository;

import dat3.personalTrainer.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
}
