package pl.marekpedrys.githubclient.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.marekpedrys.githubclient.api.models.RepoResponse;
import pl.marekpedrys.githubclient.exceptionhandling.exceptions.GitHubClientException;
import pl.marekpedrys.githubclient.exceptionhandling.models.ExceptionInfoTemplate;
import pl.marekpedrys.githubclient.httpClients.models.Branch;
import pl.marekpedrys.githubclient.httpClients.models.Repo;
import pl.marekpedrys.githubclient.httpClients.models.SearchReposGitHubResponse;
import reactor.core.publisher.Flux;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class GitHubRepoService {
    private final WebClient client;
    //TODO include pagination
    public Flux<RepoResponse> getReposWithBranches(String username, String authorizationHeader) {
        return client
                .get()
                .uri("/search/repositories?q=user:{username}", username)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(SearchReposGitHubResponse.class)
                .onErrorMap(WebClientResponseException.Forbidden.class, ex -> new GitHubClientException(ExceptionInfoTemplate.LIMIT_EXCEEDED))
                .onErrorMap(WebClientResponseException.UnprocessableEntity.class, ex -> new GitHubClientException(ExceptionInfoTemplate.USER_NOT_FOUND, username))
                .onErrorMap(WebClientResponseException.ServiceUnavailable.class, ex -> new GitHubClientException(ExceptionInfoTemplate.GITHUB_API_UNAVAILABLE))
                .map(SearchReposGitHubResponse::getItems)
                .flatMapMany(Flux::fromIterable)
                .filter(r -> !r.isFork()) // redundant?
                .flatMap(repo -> getBranchesForRepo(username, repo.getName(), authorizationHeader)
                        .collectList()
                        .map(branches -> new Repo(repo, branches)))
                .map(RepoResponse::of)
                .sort(Comparator.comparing(r -> r.getRepositoryName().toLowerCase()));
    }

    private Flux<Branch> getBranchesForRepo(String owner, String repo, String authorizationHeader) {
        return client
                .get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToFlux(Branch.class)
                .onErrorMap(WebClientResponseException.Forbidden.class, ex -> new GitHubClientException(ExceptionInfoTemplate.LIMIT_EXCEEDED))
                .onErrorMap(WebClientResponseException.UnprocessableEntity.class, ex -> new GitHubClientException(ExceptionInfoTemplate.USER_OR_REPO_NOT_FOUND, owner, repo))
                .onErrorMap(WebClientResponseException.ServiceUnavailable.class, ex -> new GitHubClientException(ExceptionInfoTemplate.GITHUB_API_UNAVAILABLE));
    }

}