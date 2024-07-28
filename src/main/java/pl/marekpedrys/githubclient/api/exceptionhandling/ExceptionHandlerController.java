package pl.marekpedrys.githubclient.api.exceptionhandling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(GithubClientException.class)
    public ResponseEntity<ExceptionInfoResponse> handleGithubScrapperException(GithubClientException e) {
        return ResponseEntity
                .status(e.getExceptionInfo().getStatus())
                .body(new ExceptionInfoResponse(e.getExceptionInfo().getStatus(),
                        String.format(e.getExceptionInfo().getMessageTemplate(), e.getMessageParameters())));
    }

}
