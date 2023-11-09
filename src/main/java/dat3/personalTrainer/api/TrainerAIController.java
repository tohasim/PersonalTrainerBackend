package dat3.personalTrainer.api;

import dat3.personalTrainer.dto.MyResponse;
import dat3.personalTrainer.service.OpenAiService;
import dat3.personalTrainer.dto.GoalResponse;
import dat3.personalTrainer.dto.UserResponse;
import dat3.personalTrainer.service.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/trainer")
@CrossOrigin(origins = "*")
public class TrainerAIController {

  private final String SYSTEM_MESSAGE = "You are a personal assistant who, based on the user's fitness, goals and age, can help with providing a good training schedule, and diet plan." +
          "When asked for a fitness plan respond only with a 3 days a week plan, with names for each days (Like \"push, pull, legs\", \"Full-body\", etc NOT JUST THE WEEKDAYS). Meaning, you should not provide any additional text, just the plan and exercises, starting with the 1st day and ending with the last. " +
          "The program should be JSON formatted as so:" +
          "{" +
          "  \"goal\": \"Gain weight\"," +
          "  \"age\": 23," +
          "  \"gender\": \"male\"," +
          "  \"weight\": 70," +
          "  \"fitness_plan\": {" +
          "    \"day1\": {" +
          "      \"name\": \"Chest and Triceps\"," +
          "      \"exercises\": [" +
          "        {" +
          "          \"name\": \"Bench Press\"," +
          "          \"sets\": 4," +
          "          \"reps\": 8" +
          "        }," +
          "        {" +
          "          \"name\": \"Incline Dumbbell Press\"," +
          "          \"sets\": 3," +
          "          \"reps\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"Dips\"," +
          "          \"sets\": 3," +
          "          \"reps\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"Triceps Pushdown\"," +
          "          \"sets\": 3," +
          "          \"reps\": 12" +
          "        }" +
          "      ]" +
          "    }," +
          "    \"day2\": {" +
          "      \"name\": \"Back and Biceps\"," +
          "      \"exercises\": [" +
          "        {" +
          "          \"name\": \"Pull-ups\"," +
          "          \"sets\": 4," +
          "          \"reps\": 8" +
          "        }," +
          "        {" +
          "          \"name\": \"Bent-Over Rows\"," +
          "          \"sets\": 3," +
          "          \"reps\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"Barbell Curls\"," +
          "          \"sets\": 3," +
          "          \"reps\": 12" +
          "        }," +
          "        {" +
          "          \"name\": \"Hammer Curls\"," +
          "          \"sets\": 3," +
          "          \"reps\": 12" +
          "        }" +
          "      ]" +
          "    }," +
          "    \"day3\": {" +
          "      \"name\": \"Legs and Shoulders\"," +
          "      \"exercises\": [" +
          "        {" +
          "          \"name\": \"Squats\"," +
          "          \"sets\": 4," +
          "          \"reps\": 8" +
          "        }," +
          "        {" +
          "          \"name\": \"Leg Press\"," +
          "          \"sets\": 3," +
          "          \"reps\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"Shoulder Press\"," +
          "          \"sets\": 3," +
          "          \"reps\": 10" +
          "        }," +
          "        {" +
          "          \"name\": \"Lateral Raises\"," +
          "          \"sets\": 3," +
          "          \"reps\": 12" +
          "        }" +
          "      ]" +
          "    }" +
          "  }" +
          "}" +
          "" +
          "For dietary plans you should only provide the plan, include a specific plan for each weekday, so monday-friday. It should be formatted like so:" +
          "{" +
          "  \"name of day\":{" +
          "    \"name of dish (like breakfast, snack1, etc)\" : \"what to eat\"," +
          "  \"name of dish\": \"What to eat\"" +
          "..." +
          "}," +
          "  \"name of day\":{" +
          "    \"name of dish (like breakfast, snack1, etc)\" : \"what to eat\"," +
          "  \"name of dish\": \"What to eat\"" +
          "..." +
          "}," +
          "  }" +
          "}" +
          "";
  private final int BUCKET_CAPACITY = 3;
  private final int REFILL_AMOUNT = 3;
  private final int REFILL_TIME = 2;

  private OpenAiService service;
  private UserService userService;

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  public TrainerAIController(OpenAiService service, UserService userService) {
    this.userService = userService;
    this.service=service;
  }

  private Bucket createNewBucket() {
    Bandwidth limit = Bandwidth.classic(BUCKET_CAPACITY, Refill.greedy(REFILL_AMOUNT, Duration.ofMinutes(REFILL_TIME)));
    return Bucket.builder().addLimit(limit).build();
  }

  private Bucket getBucket(String key) {
    return buckets.computeIfAbsent(key, k -> createNewBucket());
  }

  @GetMapping("/plan")
  public MyResponse getResponseLimited(@RequestParam String username, HttpServletRequest request) {
    UserResponse userResponse = userService.getUser(username);
    GoalResponse goal = userService.getGoalsForUser(userResponse);
    String about = String.format("Provide a fitness plan for a %s, age %d, who weighs %f kg and have the following goal: %s",
            userResponse.getGender(),
            userResponse.getAge(),
            userResponse.getWeight(),
            goal.getName());


    String ip = request.getRemoteAddr();
    Bucket bucket = getBucket(ip);
    if (!bucket.tryConsume(1)) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, try again later");
    }
    return service.makeRequest(about, SYSTEM_MESSAGE);
  }
  @GetMapping("unlimited")
  public MyResponse getResponseUnlimited(@RequestParam String about){
    return service.makeRequest(about, SYSTEM_MESSAGE);
  }
}

