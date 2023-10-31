package com.ksoot.problem.core;

import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import com.ksoot.problem.spring.config.ProblemBeanRegistry;
import jakarta.annotation.Nullable;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rajveer Singh
 */
@UtilityClass
public class ProblemUtils {

  public static String statusCode(final HttpStatusCode httpStatus) {
    return String.valueOf(httpStatus.value());
  }

  public static ThrowableProblem toProblem(final Throwable throwable) {
    final HttpStatus status = resolveStatus(throwable);
    ThrowableProblem problem = Problems.newInstance(status).detail(throwable.getMessage()).build();
    problem.setStackTrace(createStackTrace(throwable));
    return problem;
  }

  public static HttpStatus resolveStatus(final Throwable throwable) {
    return Optional.ofNullable(ProblemUtils.resolveResponseStatus(throwable))
        .map(ResponseStatus::value)
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static ResponseStatus resolveResponseStatus(final Throwable type) {
    @Nullable
    final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
    return candidate == null && type.getCause() != null
        ? resolveResponseStatus(type.getCause())
        : candidate;
  }

  public static StackTraceElement[] createStackTrace(final Throwable throwable) {
    final Throwable cause = throwable.getCause();
    if (cause == null || !ProblemBeanRegistry.problemProperties().isCauseChainsEnabled()) {
      return throwable.getStackTrace();
    } else {

      final StackTraceElement[] next = cause.getStackTrace();
      final StackTraceElement[] current = throwable.getStackTrace();

      final int length =
          current.length - Lists.lengthOfTrailingPartialSubList(asList(next), asList(current));
      final StackTraceElement[] stackTrace = new StackTraceElement[length];
      System.arraycopy(current, 0, stackTrace, 0, length);
      return stackTrace;
    }
  }

  public static String getStackTrace(final Throwable exception) {
    String stacktrace = ExceptionUtils.getStackTrace(exception);
    StringBuilder escapedStacktrace = new StringBuilder(stacktrace.length() + 100);
    StringCharacterIterator scitr = new StringCharacterIterator(stacktrace);

    char current = scitr.first();
    // DONE = \\uffff (not a character)
    String lastAppend = null;
    while (current != CharacterIterator.DONE) {
      if (current == '\t' || current == '\r' || current == '\n') {
        if (!" ".equals(lastAppend)) {
          escapedStacktrace.append(" ");
          lastAppend = " ";
        }
      } else {
        // nothing matched - just text as it is.
        escapedStacktrace.append(current);
        lastAppend = "" + current;
      }
      current = scitr.next();
    }
    return escapedStacktrace.toString();
  }
}
