package dat3.openai_demo.api;

import dat3.openai_demo.dtos.MyResponse;
import dat3.openai_demo.service.OpenAiService;
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

  private final String SYSTEM_MESSAGE = "You are a personal assistant who, based on the user's fitness, goals and age, can help with providing a good training schedule, and diet plan.\n" +
          "When asked for a fitness plan respond only with a 3 days a week plan, with names for each days (Like \"push, pull, legs\", \"Full-body\", etc NOT JUST THE WEEKDAYS). Meaning, you should not provide any additional text, just the plan and exercises, starting with the 1st day and ending with the last. \n" +
          "The program should be formatted as so:\n" +
          "{\n" +
          "  \"goal\": \"Gain weight\",\n" +
          "  \"age\": 23,\n" +
          "  \"gender\": \"male\",\n" +
          "  \"weight\": 70,\n" +
          "  \"fitness_plan\": {\n" +
          "    \"day1\": {\n" +
          "      \"name\": \"Chest and Triceps\",\n" +
          "      \"exercises\": [\n" +
          "        {\n" +
          "          \"name\": \"Bench Press\",\n" +
          "          \"sets\": 4,\n" +
          "          \"reps\": 8\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Incline Dumbbell Press\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 10\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Dips\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 10\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Triceps Pushdown\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 12\n" +
          "        }\n" +
          "      ]\n" +
          "    },\n" +
          "    \"day2\": {\n" +
          "      \"name\": \"Back and Biceps\",\n" +
          "      \"exercises\": [\n" +
          "        {\n" +
          "          \"name\": \"Pull-ups\",\n" +
          "          \"sets\": 4,\n" +
          "          \"reps\": 8\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Bent-Over Rows\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 10\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Barbell Curls\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 12\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Hammer Curls\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 12\n" +
          "        }\n" +
          "      ]\n" +
          "    },\n" +
          "    \"day3\": {\n" +
          "      \"name\": \"Legs and Shoulders\",\n" +
          "      \"exercises\": [\n" +
          "        {\n" +
          "          \"name\": \"Squats\",\n" +
          "          \"sets\": 4,\n" +
          "          \"reps\": 8\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Leg Press\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 10\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Shoulder Press\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 10\n" +
          "        },\n" +
          "        {\n" +
          "          \"name\": \"Lateral Raises\",\n" +
          "          \"sets\": 3,\n" +
          "          \"reps\": 12\n" +
          "        }\n" +
          "      ]\n" +
          "    }\n" +
          "  }\n" +
          "}\n" +
          "\n" +
          "For dietary plans you should only provide the plan, include a specific plan for each weekday, so monday-friday. It should be formatted like so:\n" +
          "{\n" +
          "  \"name of day\":{\n" +
          "    \"name of dish (like breakfast, snack1, etc)\" : \"what to eat\",\n" +
          "  \"name of dish\": \"What to eat\"\n" +
          "...\n" +
          "},\n" +
          "  \"name of day\":{\n" +
          "    \"name of dish (like breakfast, snack1, etc)\" : \"what to eat\",\n" +
          "  \"name of dish\": \"What to eat\"\n" +
          "...\n" +
          "},\n" +
          "  }\n" +
          "}" +
          "\n";
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

