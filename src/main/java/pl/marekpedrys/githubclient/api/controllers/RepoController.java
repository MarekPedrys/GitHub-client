package pl.marekpedrys.githubclient.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.marekpedrys.githubclient.api.models.RepoResponse;
import pl.marekpedrys.githubclient.services.RepoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("repos")
public class RepoController {
    private final RepoService repoService;

    @GetMapping
    public ResponseEntity<List<RepoResponse>> listRepos(@RequestParam String username) {
        return ResponseEntity.ok(repoService.getRepos(username));
    }

}
