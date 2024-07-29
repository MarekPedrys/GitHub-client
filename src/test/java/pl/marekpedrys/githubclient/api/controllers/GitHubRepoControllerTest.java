package pl.marekpedrys.githubclient.api.controllers;

import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.marekpedrys.githubclient.httpClients.feignclients.GitHubFeignClient;
import pl.marekpedrys.githubclient.httpClients.models.*;
import pl.marekpedrys.githubclient.services.GitHubRepoService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.marekpedrys.githubclient.services.GitHubRepoService.PER_PAGE;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class GitHubRepoControllerTest {
    @Autowired
    private GitHubRepoController controller;
    @Autowired
    @InjectMocks
    private GitHubRepoService gitHubRepoService;
    @MockBean
    private GitHubFeignClient feignClient;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @DisplayName("Should list those user repositories that are not forks")
    void shouldListRepos() throws Exception { //TODO include pagination
        //given
        Owner owner = new Owner("testUserLogin");
        Repo repo1 = new Repo("Repo1_notFork", false, owner, null);
        Repo repo2 = new Repo("Repo2_Fork", true, owner, null);
        Repo repo3 = new Repo("Repo3_notFork", false, owner, null);
        SearchReposGitHubResponse ghReposResponse = new SearchReposGitHubResponse(List.of(repo1, repo2, repo3));
        ResponseEntity<SearchReposGitHubResponse> ghReposResponseEntity = ResponseEntity.status(HttpStatus.OK).body(ghReposResponse);

        Branch branch1_1 = new Branch("Repo1_Branch1", new Commit("Repo1_Branch1_sha"));
        Branch branch2_1 = new Branch("Repo2_Branch1", new Commit("Repo2_Branch1_sha"));
        Branch branch3_1 = new Branch("Repo3_Branch1", new Commit("Repo3_Branch1_sha"));
        Branch branch3_2 = new Branch("Repo3_Branch2", new Commit("Repo3_Branch2_sha"));
        ResponseEntity<List<Branch>> ghBranchesResponseEntity1 = ResponseEntity.status(HttpStatus.OK).body(List.of(branch1_1));
        ResponseEntity<List<Branch>> ghBranchesResponseEntity2 = ResponseEntity.status(HttpStatus.OK).body(List.of(branch2_1));
        ResponseEntity<List<Branch>> ghBranchesResponseEntity3 = ResponseEntity.status(HttpStatus.OK).body(List.of(branch3_1, branch3_2));

        Mockito.doReturn(ghReposResponseEntity).when(feignClient).getRepos(owner.getLogin(), PER_PAGE, 1);
        Mockito.doReturn(ghBranchesResponseEntity1).when(feignClient).getBranches(owner.getLogin(), repo1.getName(), PER_PAGE, 1);
        Mockito.doReturn(ghBranchesResponseEntity2).when(feignClient).getBranches(owner.getLogin(), repo2.getName(), PER_PAGE, 1);
        Mockito.doReturn(ghBranchesResponseEntity3).when(feignClient).getBranches(owner.getLogin(), repo3.getName(), PER_PAGE, 1);

        String expectedJson = "[{\"repositoryName\":\"Repo1_notFork\",\"ownerLogin\":\"testUserLogin\",\"branches\":[{\"name\":\"Repo1_Branch1\",\"lastCommitSha\":\"Repo1_Branch1_sha\"}]},{\"repositoryName\":\"Repo3_notFork\",\"ownerLogin\":\"testUserLogin\",\"branches\":[{\"name\":\"Repo3_Branch1\",\"lastCommitSha\":\"Repo3_Branch1_sha\"},{\"name\":\"Repo3_Branch2\",\"lastCommitSha\":\"Repo3_Branch2_sha\"}]}]";

        //when-then
        MvcResult mvcResult = mockMvc.perform(get("/repos").param("username", owner.getLogin()))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Should inform that there is no GitHub user with the given username")
    void shouldProcessIncorrectName() throws Exception {
        //given
        Owner owner = new Owner("testUserLogin");
        Mockito.when(feignClient.getRepos(owner.getLogin(), PER_PAGE, 1))
                .thenThrow(FeignException.UnprocessableEntity.class);

        String expectedJson = "{\"status\":\"NOT_FOUND\",\"message\":\"user 'testUserLogin' is not an existing github user\"}";

        //when-then
        MvcResult mvcResult = mockMvc.perform(get("/repos").param("username", owner.getLogin()))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString());
    }

}
