package pl.marekpedrys.githubclient.exceptionhandling.models;

import org.springframework.http.HttpStatus;

public record ExceptionInfoResponse(HttpStatus status, String message) {
}
