package pl.marekpedrys.githubclient.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.marekpedrys.githubclient.api.models.RepoResponse;
import pl.marekpedrys.githubclient.services.GitHubRepoService;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("repos")
public class GitHubRepoController {
    private final GitHubRepoService gitHubRepoService;

    @GetMapping
    public ResponseEntity<Flux<RepoResponse>> listRepos(@RequestParam String username,
                                                        @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        return ResponseEntity.ok(gitHubRepoService.getReposWithBranches(username, authorizationHeader));
    }

}
