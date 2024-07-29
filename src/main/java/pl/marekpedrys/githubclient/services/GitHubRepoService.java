package pl.marekpedrys.githubclient.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.marekpedrys.githubclient.exceptionhandling.models.ExceptionInfoTemplate;
import pl.marekpedrys.githubclient.exceptionhandling.exceptions.GitHubClientException;
import pl.marekpedrys.githubclient.api.models.RepoResponse;
import pl.marekpedrys.githubclient.httpClients.feignclients.GitHubFeignClient;
import pl.marekpedrys.githubclient.httpClients.models.Branch;
import pl.marekpedrys.githubclient.httpClients.models.Repo;
import pl.marekpedrys.githubclient.httpClients.models.SearchReposGitHubResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubRepoService {
    private final GitHubFeignClient feignClient;
    public final static int PER_PAGE = 100;

    public List<RepoResponse> getRepos(String username) {
        List<Repo> userRepos = getReposBasicInfo(username);
        userRepos.removeIf(Repo::isFork); //TODO redundant?
        addReposBranchesInfo(userRepos);
        return userRepos.stream()
                .map(RepoResponse::of)
                .sorted(Comparator.comparing(RepoResponse::getRepositoryName))
                .toList();
    }

    private List<Repo> getReposBasicInfo(String username) {
        List<Repo> userRepos = new ArrayList<>();
        ResponseEntity<SearchReposGitHubResponse> ghResponse;
        List<Repo> ghBody;
        HttpHeaders ghHeaders;
        int page = 1;
        do {
            try {
                ghResponse = feignClient.getRepos(username, PER_PAGE, page++);
            } catch (FeignException.UnprocessableEntity e) {
                throw new GitHubClientException(ExceptionInfoTemplate.USER_NOT_FOUND, username);
            } catch (FeignException.Forbidden e) {
                throw new GitHubClientException(ExceptionInfoTemplate.LIMIT_EXCEEDED);
            } catch (FeignException.ServiceUnavailable e) {
                throw new GitHubClientException(ExceptionInfoTemplate.GITHUB_API_UNAVAILABLE);
            }
            ghBody = ghResponse.getBody().getItems();
            ghHeaders = ghResponse.getHeaders();
            userRepos.addAll(ghBody);
        } while (linkToNextPageExists(ghHeaders));
        return userRepos;
    }

    private void addReposBranchesInfo(List<Repo> repos) {
        ResponseEntity<List<Branch>> ghResponse;
        List<Branch> ghBody;
        HttpHeaders ghHeaders;
        for (Repo repo : repos) {
            repo.setBranches(new ArrayList<>());
            int page = 1;
            do {
                try {
                    ghResponse = feignClient.getBranches(repo.getOwner().getLogin(), repo.getName(), PER_PAGE, page++);
                } catch (FeignException.NotFound e) {
                    throw new GitHubClientException(ExceptionInfoTemplate.USER_OR_REPO_NOT_FOUND, repo.getOwner().getLogin(), repo.getName());
                } catch (FeignException.Forbidden e) {
                    throw new GitHubClientException(ExceptionInfoTemplate.LIMIT_EXCEEDED);
                }
                ghBody = ghResponse.getBody();
                ghHeaders = ghResponse.getHeaders();
                repo.getBranches().addAll(ghBody);
            } while (linkToNextPageExists(ghHeaders));
        }
    }

    private boolean linkToNextPageExists(HttpHeaders ghHeaders) {
        List<String> links = ghHeaders.getOrEmpty("Link");
        return !links.isEmpty() &&
                links.get(0) != null &&
                links.get(0).contains("rel=\"next\"");
    }

}
