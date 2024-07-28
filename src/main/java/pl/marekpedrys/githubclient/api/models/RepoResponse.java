package pl.marekpedrys.githubclient.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.marekpedrys.githubclient.httpClients.models.Repo;

import java.util.List;

@AllArgsConstructor
@Getter
public class RepoResponse {
    private String repositoryName;
    private String ownerLogin;
    private List<BranchResponse> branches;

    public static RepoResponse of(Repo repo) {
        List<BranchResponse> branchResponses = repo.getBranches().stream()
                .map(BranchResponse::of)
                .toList();
        return new RepoResponse(
                repo.getName(),
                repo.getOwner().getLogin(),
                branchResponses);
    }
}
