package com.ksoot.problem.spring.advice;

import com.ksoot.problem.core.Problem;
import com.ksoot.problem.core.ProblemUtils;
import com.ksoot.problem.core.ThrowableProblem;
import com.ksoot.problem.spring.config.ProblemBeanRegistry;
import com.ksoot.problem.spring.config.ProblemMessageProvider;
import com.ksoot.problem.spring.config.ProblemMessageSourceResolver;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.ksoot.problem.core.ProblemConstant.CODE_RESOLVER;
import static com.ksoot.problem.core.ProblemConstant.DETAIL_RESOLVER;
import static com.ksoot.problem.core.ProblemConstant.STACKTRACE_KEY;
import static com.ksoot.problem.core.ProblemConstant.STATUS_RESOLVER;
import static com.ksoot.problem.core.ProblemConstant.TITLE_RESOLVER;

/**
 * <p>
 * Advice traits are simple interfaces that provide a single method with a
 * default implementation. They are used to provide {@link ExceptionHandler}
 * implementations to be used in Spring Controllers and/or in a
 * {@link ControllerAdvice}. Clients can choose which traits they what to use à
 * la carte.
 * </p>
 * <p>
 * Advice traits are grouped in packages, based on they use cases. Every package
 * has a composite advice trait that bundles all traits of that package.
 * </p>
 *
 * @see ControllerAdvice
 * @see ExceptionHandler
 * @see Throwable
 * @see Exception
 * @see Problem
 */
public interface BaseAdviceTrait {

  default ThrowableProblem toProblem(final Throwable throwable) {
    HttpStatus status = HttpStatus.valueOf(ProblemUtils.resolveStatus(throwable).value());
    return toProblem(throwable, status);
  }

  default ThrowableProblem toProblem(final Throwable throwable, final HttpStatus status) {
    return toProblem(throwable, status, throwable.getMessage());
  }

  default ThrowableProblem toProblem(final Throwable throwable, final HttpStatus status, final String detail) {
    return toProblem(throwable, String.valueOf(status.value()), status.getReasonPhrase(), detail);
  }

  default ThrowableProblem toProblem(final Throwable throwable, final String code, final String title,
                                     final String detail) {
    return toProblem(throwable, code, title, detail, new LinkedHashMap<>());
  }

  default ThrowableProblem toProblem(final Throwable throwable, final String code, final String title,
                                     final String detail, final Map<String, Object> parameters) {
    final ThrowableProblem problem = prepare(throwable, code, title, detail, parameters);
    final StackTraceElement[] stackTrace = ProblemUtils.createStackTrace(throwable);
    problem.setStackTrace(stackTrace);
    return problem;
  }

  default ThrowableProblem toProblem(final Throwable throwable, final HttpStatus status,
                                     final MessageSourceResolvable detailResolver) {
    return toProblem(throwable, String.valueOf(status.value()), status.getReasonPhrase(), detailResolver);
  }

  default ThrowableProblem toProblem(final Throwable throwable, final String code, final String title,
                                     final MessageSourceResolvable detailResolver) {
    Map<String, Object> parameters = new LinkedHashMap<>();
    if (ProblemBeanRegistry.problemProperties().isDebugEnabled()) {
      parameters.put(DETAIL_RESOLVER, detailResolver);
    }
    return toProblem(throwable, code, title, ProblemMessageProvider.getMessage(detailResolver), parameters);
  }

  default ThrowableProblem toProblem(final Throwable throwable, final String code,
                                     final MessageSourceResolvable titleResolver, final MessageSourceResolvable detailResolver) {
    Map<String, Object> parameters = new LinkedHashMap<>();
    if (ProblemBeanRegistry.problemProperties().isDebugEnabled()) {
      parameters.put(TITLE_RESOLVER, titleResolver);
      parameters.put(DETAIL_RESOLVER, detailResolver);
    }
    return toProblem(throwable, code, ProblemMessageProvider.getMessage(titleResolver),
        ProblemMessageProvider.getMessage(detailResolver), parameters);
  }

  default ThrowableProblem toProblem(final Throwable throwable, final MessageSourceResolvable codeResolver,
                                     final MessageSourceResolvable titleResolver, final MessageSourceResolvable detailResolver) {
    return toProblem(throwable, codeResolver, titleResolver, detailResolver, new LinkedHashMap<>());
  }

  default ThrowableProblem toProblem(final Throwable throwable, final MessageSourceResolvable codeResolver,
                                     final MessageSourceResolvable titleResolver, final MessageSourceResolvable detailResolver,
                                     final Map<String, Object> parameters) {
    if (ProblemBeanRegistry.problemProperties().isDebugEnabled()) {
      parameters.put(CODE_RESOLVER, codeResolver);
      parameters.put(TITLE_RESOLVER, titleResolver);
      parameters.put(DETAIL_RESOLVER, detailResolver);
    }
    return toProblem(throwable, ProblemMessageProvider.getMessage(codeResolver), ProblemMessageProvider.getMessage(titleResolver),
        ProblemMessageProvider.getMessage(detailResolver), parameters);
  }

  default ThrowableProblem toProblem(final Throwable throwable, final MessageSourceResolvable codeResolver,
                                     final MessageSourceResolvable titleResolver, final MessageSourceResolvable detailResolver,
                                     final ProblemMessageSourceResolver statusResolver) {
    Map<String, Object> parameters = new LinkedHashMap<>();
    if (ProblemBeanRegistry.problemProperties().isDebugEnabled()) {
      parameters.put(CODE_RESOLVER, codeResolver);
      parameters.put(TITLE_RESOLVER, titleResolver);
      parameters.put(DETAIL_RESOLVER, detailResolver);
      parameters.put(STATUS_RESOLVER, statusResolver);
    }
    return toProblem(throwable, ProblemMessageProvider.getMessage(codeResolver), ProblemMessageProvider.getMessage(titleResolver),
        ProblemMessageProvider.getMessage(detailResolver), parameters);
  }


  default ThrowableProblem prepare(final Throwable throwable, final String code, final String title,
                                   final String detail, final Map<String, Object> parameters) {
    if (ProblemBeanRegistry.problemProperties().isStacktraceEnabled()) {
      final StackTraceElement[] stackTrace = ProblemUtils.createStackTrace(throwable);
      parameters.put(STACKTRACE_KEY, stackTrace);
    }
    return Problem.code(code).title(title).detail(detail)
        .cause(Optional.ofNullable(throwable.getCause())
            .filter(cause -> ProblemBeanRegistry.problemProperties().isCauseChainsEnabled())
            .map(this::toProblem).orElse(null))
        .parameters(Collections.unmodifiableMap(parameters)).build();
  }
}
