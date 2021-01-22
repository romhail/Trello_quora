package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * AnswerRequest
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-01-16T11:29:32.003+05:30")

public class AnswerRequest   {
  @JsonProperty("answer")
  private String answer = null;

  public AnswerRequest answer(String answer) {
    this.answer = answer;
    return this;
  }

  /**
   * answer to the question
   * @return answer
  **/
  @ApiModelProperty(required = true, value = "answer to the question")
  @NotNull


  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnswerRequest answerRequest = (AnswerRequest) o;
    return Objects.equals(this.answer, answerRequest.answer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnswerRequest {\n");

    sb.append("    answer: ").append(toIndentedString(answer)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

