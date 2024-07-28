package pl.marekpedrys.githubclient.httpClients.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Repo {
    private String name;
    private boolean fork;
    private Owner owner;
    private List<Branch> branches = new ArrayList<>();
}
