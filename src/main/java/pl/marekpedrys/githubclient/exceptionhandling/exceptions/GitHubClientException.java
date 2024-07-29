package pl.marekpedrys.githubclient.exceptionhandling.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.marekpedrys.githubclient.exceptionhandling.models.ExceptionInfoTemplate;

@Getter
@RequiredArgsConstructor
public class GitHubClientException extends RuntimeException {
    private final ExceptionInfoTemplate exceptionInfoTemplate;
    private String[] messageParameters;

    public GitHubClientException(ExceptionInfoTemplate exceptionInfoTemplate, String... messageParameters) {
        this.exceptionInfoTemplate = exceptionInfoTemplate;
        this.messageParameters = messageParameters;
    }

}
