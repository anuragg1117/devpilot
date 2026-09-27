package devpilot.backend.dto;

import java.time.Instant;
import java.util.UUID;

import devpilot.backend.entity.IndexStatus;
import devpilot.backend.entity.Repository;

public record RepositoryResponse(

        UUID id,

        Long githubRepoId,

        String owner,

        String name,

        String fullName,

        boolean isPrivate,

        String defaultBranch,

        String language,

        String htmlUrl,

        String description,

        IndexStatus indexStatus,

        Instant indexedAt,

        int chunkCount,

        int filesTotal,

        int filesProcessed,

        String errorMessage,

        Instant createdAt,

        Instant updatedAt
) {

    public static RepositoryResponse from(
            Repository repository) {

        return new RepositoryResponse(
                repository.getId(),
                repository.getGithubRepoId(),
                repository.getOwner(),
                repository.getName(),
                repository.getFullName(),
                repository.isPrivate(),
                repository.getDefaultBranch(),
                repository.getLanguage(),
                repository.getHtmlUrl(),
                repository.getDescription(),
                repository.getIndexStatus(),
                repository.getIndexedAt(),
                repository.getChunkCount(),
                repository.getFilesTotal(),
                repository.getFilesProcessed(),
                repository.getErrorMessage(),
                repository.getCreatedAt(),
                repository.getUpdatedAt()
        );
    }
}