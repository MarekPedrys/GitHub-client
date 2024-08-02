package pl.marekpedrys.githubclient.httpClients.webClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConf {
    @Value("${github.url}")
    public String gitHubApiUrl;

    @Bean
    public WebClient getGitHubWebClient() {
        return WebClient.builder()
                .baseUrl(gitHubApiUrl)
                .build();
    }

}
