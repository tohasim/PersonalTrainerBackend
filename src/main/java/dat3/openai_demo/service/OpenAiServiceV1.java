package dat3.openai_demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat3.openai_demo.dtos.ChatCompletionResponse;
import dat3.openai_demo.dtos.MyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
Shows an alternative way to create the request body, using a Map<String,Object> instead of a DTO
Observe the other version provides better error handling. Add this if you want to experiment with this strategy.
 */
@Service
public class OpenAiServiceV1 {

  @Value("${app.openai-api-key}")
  private String API_KEY;

  //See here for a decent explanation of the parameters send to the API via the requestBody
  //https://platform.openai.com/docs/api-reference/completions/create

  public final static String URL = "https://api.openai.com/v1/chat/completions";
  public final static String MODEL = "gpt-3.5-turbo";
  public final static double TEMPERATURE = 0.8;
  public final static int MAX_TOKENS = 300;
  public final static double FREQUENCY_PENALTY = 0.2;
  public final static double PRESENCE_PENALTY = 0;

  public MyResponse makeRequest(String userPrompt, String _systemMessage) {
    WebClient client = WebClient.create();

    Map<String, Object> body = new HashMap<>();

    body.put("model", MODEL);
    List<Map<String, String>> messages = new ArrayList<>();

    Map<String, String> systemMessage = new HashMap<>();
    systemMessage.put("role", "system");
    systemMessage.put("content", _systemMessage);
    messages.add(systemMessage);

    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", userPrompt);
    messages.add(userMessage);
    body.put("messages", messages);
    body.put("temperature", TEMPERATURE);
    body.put("max_tokens", MAX_TOKENS);
    body.put("top_p", 1);
    body.put("frequency_penalty", FREQUENCY_PENALTY);
    body.put("presence_penalty", PRESENCE_PENALTY);
    ObjectMapper mapper = new ObjectMapper();
    String json = "";

    try {
      json = mapper.writeValueAsString(body);
      System.out.println(json);
      ChatCompletionResponse response = client.post()
              .uri(new URI(URL))
              .header("Authorization", "Bearer " + API_KEY)
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(json))
              .retrieve()
              .bodyToMono(ChatCompletionResponse.class)
              .block();
      //System.out.println(response.toString());
      String responseMsg = response.getChoices().get(0).getMessage().getContent();
      int tokensUsed = response.getUsage().getTotal_tokens();
      System.out.print("Tokens used: " + tokensUsed);
      System.out.print(". Cost ($0.0015 / 1K tokens) : $" + String.format("%6f",(tokensUsed * 0.0015 / 1000)));
      System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1/(tokensUsed * 0.0015 / 1000)));
      return new MyResponse(responseMsg);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      String msg = "Internal Server Error, while processing request. You could try again"+
                   "( While you develop, make sure to consult the detailed error message on your backend)";
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, msg);

    }
  }
}
