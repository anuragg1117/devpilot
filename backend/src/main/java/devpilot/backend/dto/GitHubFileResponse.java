package devpilot.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubFileResponse(
        String name,
        String path,
        String sha,
        Integer size,
        String encoding,
        String content,
        @JsonProperty("download_url")
        String downloadUrl
) {
}