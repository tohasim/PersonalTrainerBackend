package dat3.personalTrainer.repository;

import dat3.personalTrainer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
