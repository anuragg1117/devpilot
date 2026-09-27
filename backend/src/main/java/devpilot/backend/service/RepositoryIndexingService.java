package devpilot.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devpilot.backend.dto.CodeChunk;
import devpilot.backend.dto.GitHubFileResponse;
import devpilot.backend.dto.GitHubTreeResponse;
import devpilot.backend.entity.IndexStatus;
import devpilot.backend.entity.Repository;
import devpilot.backend.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepositoryIndexingService {

    private final RepositoryService repositoryService;
    private final UserService userService;
    private final GitHubRepositoryClient githubRepositoryClient;
    private final CodeFileFilter codeFileFilter;
    private final CodeChunker codeChunker;

    @Transactional
    public List<CodeChunk> indexRepository(
            UUID repositoryId,
            UUID userId) {

        Repository repository =
                repositoryService.getRepository(repositoryId, userId);

        User user = userService.requiredById(userId);

        String accessToken =
                userService.decryptAccessToken(user);

        repository.setIndexStatus(IndexStatus.INDEXING);
        repository.setErrorMessage(null);
        repository.setFilesProcessed(0);
        repository.setFilesTotal(0);
        repository.setChunkCount(0);

        try {

            GitHubTreeResponse tree =
                    githubRepositoryClient.getRepositoryTree(
                            accessToken,
                            repository.getOwner(),
                            repository.getName(),
                            repository.getDefaultBranch()
                    );

            List<GitHubTreeResponse.TreeItem> files =
                    tree.tree()
                            .stream()
                            .filter(item -> "blob".equals(item.type()))
                            .filter(item ->
                                    codeFileFilter.isSupported(
                                            item.path(),
                                            item.size()
                                    ))
                            .toList();

            repository.setFilesTotal(files.size());

            List<CodeChunk> allChunks = new ArrayList<>();

            for (GitHubTreeResponse.TreeItem file : files) {

                GitHubFileResponse response =
                        githubRepositoryClient.getFile(
                                accessToken,
                                repository.getOwner(),
                                repository.getName(),
                                file.path(),
                                repository.getDefaultBranch()
                        );

                if (response == null || response.content() == null) {
                    continue;
                }

                String content = decodeContent(response.content());

                List<CodeChunk> chunks =
                        codeChunker.chunk(
                                file.path(),
                                content
                        );

                allChunks.addAll(chunks);

                repository.setFilesProcessed(
                        repository.getFilesProcessed() + 1
                );

                repository.setChunkCount(
                        allChunks.size()
                );
            }

            repository.setIndexStatus(IndexStatus.COMPLETED);
            repository.setIndexedAt(Instant.now());

            return allChunks;

        } catch (Exception exception) {

            repository.setIndexStatus(IndexStatus.FAILED);
            repository.setErrorMessage(
                    exception.getMessage()
            );

            throw exception;
        }
    }

    private String decodeContent(String content) {

        String normalizedContent =
                content.replaceAll("\\s", "");

        byte[] decoded =
                Base64.getDecoder().decode(normalizedContent);

        return new String(
                decoded,
                StandardCharsets.UTF_8
        );
    }
}