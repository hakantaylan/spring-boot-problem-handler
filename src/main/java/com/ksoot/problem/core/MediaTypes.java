package com.ksoot.problem.core;

import org.springframework.http.MediaType;

public final class MediaTypes {

  public static final String PROBLEM_VALUE = "application/problem+json";
  public static final MediaType PROBLEM = MediaType.parseMediaType(PROBLEM_VALUE);

  public static final String X_PROBLEM_VALUE = "application/x.problem+json";
  public static final MediaType X_PROBLEM = MediaType.parseMediaType(X_PROBLEM_VALUE);

  @Deprecated static final String WILDCARD_JSON_VALUE = "application/*+json";

  @Deprecated static final MediaType WILDCARD_JSON = MediaType.parseMediaType(WILDCARD_JSON_VALUE);

  private MediaTypes() {}
}
