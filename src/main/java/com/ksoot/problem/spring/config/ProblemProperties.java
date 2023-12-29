package com.ksoot.problem.spring.config;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Rajveer Singh
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@ConfigurationProperties(prefix = "problem")
public class ProblemProperties {

  /** Default: true, Whether or not to enable Problem handling. */
  private boolean enabled = true;

  /** Default: http://localhost:8080/problems/help.html, Help page base url. */
  private String typeUrl = "http://localhost:8080/problems/help.html";

  /**
   * Default: false, Whether or not to include debug-info such as message codes etc. in error
   * response messages.
   */
  private boolean debugEnabled = false;

  /** Default: false, Whether or not to include stacktrace in error response messages. */
  private boolean stacktraceEnabled = false;

  /** Default: false, Whether or not to include exception cause in error response messages. */
  private boolean causeChainsEnabled = false;

  /** Default: true, Whether or not to Enable Jackson Problem module. */
  private boolean jacksonModuleEnabled = true;

  /** Default: true, Whether or not to Enable DAO exception handling advices. */
  private boolean daoAdviceEnabled = true;

  /** Default: true, Whether or not to Enable Security exception handling advices. */
  private boolean securityAdviceEnabled = true;

  private OpenApi openApi = new OpenApi();

  @Getter
  @Setter
  @NoArgsConstructor
  @ToString
  @Valid
  public static class OpenApi {

    /** Default: /oas/api.json, Path of API Specification json file. */
    private String path = "/oas/api.json";

    /**
     * Default: None. List of path patterns in ant-pattern format to exclude from OpenAPI
     * Specification validation.
     */
    private List<String> excludePatterns = new ArrayList<>();

    /**
     * Default: true, Whether or not to enable Open API request validation.</br>While enabling make
     * sure Problem is also enabled.
     */
    private boolean reqValidationEnabled = false;

    /**
     * Default: false, Whether or not to enable Open API response validation.</br>While enabling
     * make sure Problem is also enabled.
     */
    private boolean resValidationEnabled = false;
  }
}
