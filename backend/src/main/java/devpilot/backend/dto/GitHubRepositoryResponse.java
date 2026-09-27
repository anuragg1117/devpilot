package devpilot.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubRepositoryResponse(
        Long id,
        String name,

        @JsonProperty("full_name")
        String fullName,

        Owner owner,

        @JsonProperty("private")
        boolean isPrivate,

        @JsonProperty("default_branch")
        String defaultBranch,

        String language,

        @JsonProperty("html_url")
        String htmlUrl,

        String description
) {
    public record Owner(String login) {
    }
}