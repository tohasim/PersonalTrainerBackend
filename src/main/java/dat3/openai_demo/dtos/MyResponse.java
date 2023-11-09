package dat3.openai_demo.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyResponse {
  JsonNode answer;
  List<Map<String, String>> messages;

  public MyResponse(JsonNode answer) {
    this.answer = answer;
  }
  public MyResponse(JsonNode answer, List<Map<String,String>> messages) {
    this.answer = answer;
    this.messages = messages;
  }
}
