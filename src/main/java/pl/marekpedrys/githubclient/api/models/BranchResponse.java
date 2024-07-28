package pl.marekpedrys.githubclient.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.marekpedrys.githubclient.httpClients.models.Branch;

@AllArgsConstructor
@Getter
public class BranchResponse {
    private String name;
    private String lastCommitSha;

    public static BranchResponse of(Branch branch) {
        return new BranchResponse(branch.getName(), branch.getCommit().getSha());
    }
}
