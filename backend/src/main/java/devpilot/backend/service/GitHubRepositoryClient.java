package devpilot.backend.service;

import java.util.Arrays;
import java.util.List;

import devpilot.backend.dto.GitHubFileResponse;
import devpilot.backend.dto.GitHubTreeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import devpilot.backend.dto.GitHubRepositoryResponse;

@Component
public class GitHubRepositoryClient {

    private final RestClient restClient;

    public GitHubRepositoryClient(
            RestClient.Builder restClientBuilder,
            @Value("${github.api.base-url}") String baseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public List<GitHubRepositoryResponse> getRepositories(String accessToken) {

        GitHubRepositoryResponse[] repositories = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("visibility", "all")
                        .queryParam("affiliation", "owner,collaborator,organization_member")
                        .queryParam("per_page", 100)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(GitHubRepositoryResponse[].class);

        return repositories == null
                ? List.of()
                : Arrays.asList(repositories);
    }

    public GitHubTreeResponse getRepositoryTree(
            String accessToken,
            String owner,
            String repository,
            String branch) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/git/trees/{branch}")
                        .queryParam("recursive", "1")
                        .build(owner, repository, branch))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(GitHubTreeResponse.class);
    }

    public GitHubFileResponse getFile(
            String accessToken,
            String owner,
            String repository,
            String path,
            String branch) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/contents/{path}")
                        .queryParam("ref", branch)
                        .build(owner, repository, path))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(GitHubFileResponse.class);
    }


}