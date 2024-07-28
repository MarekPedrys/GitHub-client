package pl.marekpedrys.githubclient.api.exceptionhandling;

import org.springframework.http.HttpStatus;

public record ExceptionInfoResponse(HttpStatus status, String message) {
}
