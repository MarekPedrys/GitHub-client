package pl.marekpedrys.githubclient.api.exceptionhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionInfo {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user '%s' is not an existing github user"),
    USER_OR_REPO_NOT_FOUND(HttpStatus.NOT_FOUND, "user '%s' is not an existing github user or does not have the '%s' repository"),
    LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "API rate limit exceeded. Authenticated requests get a higher rate limit. Consider using a bearer token.");

    private final HttpStatus status;
    private final String messageTemplate;
}
