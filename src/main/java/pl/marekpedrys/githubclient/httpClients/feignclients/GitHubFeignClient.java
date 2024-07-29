package pl.marekpedrys.githubclient.httpClients.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.marekpedrys.githubclient.httpClients.models.Branch;
import pl.marekpedrys.githubclient.httpClients.models.SearchReposGitHubResponse;

import java.util.List;

@FeignClient(name = "github-feign-client", url = "https://api.github.com")
public interface GitHubFeignClient {
    //TODO consider "NOT is:fork"
    @RequestMapping(method = RequestMethod.GET, value = "/search/repositories?q=user:{username}")
    ResponseEntity<SearchReposGitHubResponse> getRepos(@PathVariable String username,
                                                       @RequestParam int per_page,
                                                       @RequestParam int page);

    @RequestMapping(method = RequestMethod.GET, value = "/repos/{owner}/{repo}/branches")
    ResponseEntity<List<Branch>> getBranches(@PathVariable String owner,
                                             @PathVariable String repo,
                                             @RequestParam int per_page,
                                             @RequestParam int page);

}
