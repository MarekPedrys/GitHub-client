package pl.marekpedrys.githubclient.exceptionhandling.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionInfoTemplate {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user '%s' is not an existing github user"),
    USER_OR_REPO_NOT_FOUND(HttpStatus.NOT_FOUND, "user '%s' is not an existing github user or does not have the '%s' repository"),
    LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "API rate limit exceeded. Authenticated requests get a higher rate limit. Consider using an authorization header."),
    GITHUB_API_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "GitHub service unavailable");

    private final HttpStatus status;
    private final String messageTemplate;
}
