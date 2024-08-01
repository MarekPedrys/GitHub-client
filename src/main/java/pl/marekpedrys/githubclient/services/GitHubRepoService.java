package pl.marekpedrys.githubclient.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.marekpedrys.githubclient.api.models.RepoResponse;
import pl.marekpedrys.githubclient.exceptionhandling.exceptions.GitHubClientException;
import pl.marekpedrys.githubclient.exceptionhandling.models.ExceptionInfoTemplate;
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
                .sorted(Comparator.comparing(r -> r.getRepositoryName().toLowerCase()))
                .toList();
    }

    private List<Repo> getReposBasicInfo(String username) {
        List<Repo> userRepos = new ArrayList<>();
        ResponseEntity<SearchReposGitHubResponse> ghResponse;
        List<Repo> ghBody;
        try {
            ghResponse = feignClient.getRepos(username, PER_PAGE);
        } catch (FeignException.UnprocessableEntity e) {
            throw new GitHubClientException(ExceptionInfoTemplate.USER_NOT_FOUND, username);
        } catch (FeignException.Forbidden e) {
            throw new GitHubClientException(ExceptionInfoTemplate.LIMIT_EXCEEDED);
        } catch (FeignException.ServiceUnavailable e) {
            throw new GitHubClientException(ExceptionInfoTemplate.GITHUB_API_UNAVAILABLE);
        }
        ghBody = ghResponse.getBody().getItems();
        userRepos.addAll(ghBody);
        return userRepos;
    }

    private void addReposBranchesInfo(List<Repo> repos) {
        ResponseEntity<List<Branch>> ghResponse;
        List<Branch> ghBody;
        for (Repo repo : repos) {
            repo.setBranches(new ArrayList<>());
            try {
                ghResponse = feignClient.getBranches(repo.getOwner().getLogin(), repo.getName(), PER_PAGE);
            } catch (FeignException.NotFound e) {
                throw new GitHubClientException(ExceptionInfoTemplate.USER_OR_REPO_NOT_FOUND, repo.getOwner().getLogin(), repo.getName());
            } catch (FeignException.Forbidden e) {
                throw new GitHubClientException(ExceptionInfoTemplate.LIMIT_EXCEEDED);
            }
            ghBody = ghResponse.getBody();
            repo.getBranches().addAll(ghBody);
        }
    }

}
