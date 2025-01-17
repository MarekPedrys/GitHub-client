package pl.marekpedrys.githubclient.httpClients.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repo {
    private String name;
    private boolean fork;
    private Owner owner;
    private List<Branch> branches = new ArrayList<>();

    public Repo(Repo repo, List<Branch> branches) {
        this.name = repo.name;
        this.fork = repo.fork;
        this.owner = repo.owner;
        this.branches = branches;
    }
}
