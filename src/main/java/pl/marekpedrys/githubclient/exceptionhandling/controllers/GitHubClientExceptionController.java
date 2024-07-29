package pl.marekpedrys.githubclient.exceptionhandling.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.marekpedrys.githubclient.exceptionhandling.models.ExceptionInfoResponse;
import pl.marekpedrys.githubclient.exceptionhandling.exceptions.GitHubClientException;

@RestControllerAdvice
public class GitHubClientExceptionController {

    @ExceptionHandler(GitHubClientException.class)
    public ResponseEntity<ExceptionInfoResponse> handleGithubScrapperException(GitHubClientException e) {
        return ResponseEntity
                .status(e.getExceptionInfoTemplate().getStatus())
                .body(new ExceptionInfoResponse(e.getExceptionInfoTemplate().getStatus(),
                        String.format(e.getExceptionInfoTemplate().getMessageTemplate(), e.getMessageParameters())));
    }

}
