package devpilot.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import devpilot.backend.dto.GitHubRepositoryResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitHubRepositoryClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    public List<GitHubRepositoryResponse> getRepositories(
            String accessToken) {

        RestClient restClient = restClientBuilder
                .baseUrl(githubApiBaseUrl)
                .build();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("per_page", 100)
                        .queryParam("sort", "updated")
                        .build())
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .header(
                        HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<GitHubRepositoryResponse>>() {
                        }
                );
    }
}