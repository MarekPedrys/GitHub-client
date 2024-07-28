package pl.marekpedrys.githubclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GitHubClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitHubClientApplication.class, args);
	}

}
