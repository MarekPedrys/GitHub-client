package pl.marekpedrys.githubclient.httpClients.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchReposGitHubResponse {
    private List<Repo> items;
}
