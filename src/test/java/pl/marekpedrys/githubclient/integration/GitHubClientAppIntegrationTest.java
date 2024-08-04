package pl.marekpedrys.githubclient.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.marekpedrys.githubclient.httpClients.models.*;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8181)
class GitHubClientAppIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Should list and sort by name those user repositories that are not forks")
    void shouldListRepos() throws Exception {
        //given
        Owner owner = new Owner("testUserLogin");
        Repo repo1 = new Repo("Repo_Z_notFork", false, owner, null);
        Repo repo2 = new Repo("Repo_A_Fork", true, owner, null);
        Repo repo3 = new Repo("Repo_b_notFork", false, owner, null);
        Branch branch1_1 = new Branch("Repo1_Branch1", new Commit("Repo1_Branch1_sha"));
        Branch branch2_1 = new Branch("Repo2_Branch1", new Commit("Repo2_Branch1_sha"));
        Branch branch3_1 = new Branch("Repo3_Branch1", new Commit("Repo3_Branch1_sha"));
        Branch branch3_2 = new Branch("Repo3_Branch2", new Commit("Repo3_Branch2_sha"));

        stubFor(get(urlEqualTo(String.format("/search/repositories?q=user:%s", owner.getLogin())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(new SearchReposGitHubResponse(List.of(repo1, repo2, repo3))))));

        stubFor(get(String.format("/repos/%s/%s/branches", owner.getLogin(), repo1.getName()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(List.of(branch1_1)))));

        stubFor(get(String.format("/repos/%s/%s/branches", owner.getLogin(), repo2.getName()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(List.of(branch2_1)))));

        stubFor(get(String.format("/repos/%s/%s/branches", owner.getLogin(), repo3.getName()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(List.of(branch3_1, branch3_2)))));

        String expectedJson = "[{\"repositoryName\":\"Repo_b_notFork\",\"ownerLogin\":\"testUserLogin\",\"branches\":[{\"name\":\"Repo3_Branch1\",\"lastCommitSha\":\"Repo3_Branch1_sha\"},{\"name\":\"Repo3_Branch2\",\"lastCommitSha\":\"Repo3_Branch2_sha\"}]},{\"repositoryName\":\"Repo_Z_notFork\",\"ownerLogin\":\"testUserLogin\",\"branches\":[{\"name\":\"Repo1_Branch1\",\"lastCommitSha\":\"Repo1_Branch1_sha\"}]}]";

        //when-then
        webTestClient.get().uri("/repos?username=testUserLogin")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);
    }

    @Test
    @DisplayName("Should inform that there is no GitHub user with the given username")
    void shouldProcessIncorrectName() {
        //given
        Owner owner = new Owner("testUserLogin");

        stubFor(get(urlEqualTo(String.format("/search/repositories?q=user:%s", owner.getLogin())))
                .willReturn(aResponse()
                        .withStatus(422)));

        String expectedJson = "{\"status\":\"NOT_FOUND\",\"message\":\"user 'testUserLogin' is not an existing github user\"}";

        //when-then
        webTestClient.get().uri("/repos?username=testUserLogin")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().json(expectedJson);
    }

}
