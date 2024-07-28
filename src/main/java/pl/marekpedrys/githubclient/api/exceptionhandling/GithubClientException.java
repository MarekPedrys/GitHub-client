package pl.marekpedrys.githubclient.api.exceptionhandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GithubClientException extends RuntimeException {
    private final ExceptionInfo exceptionInfo;
    private String[] messageParameters;

    public GithubClientException(ExceptionInfo exceptionInfo, String... messageParameters) {
        this.exceptionInfo = exceptionInfo;
        this.messageParameters = messageParameters;
    }

}
