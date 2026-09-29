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
import devpilot.backend.repository.CodeChunkRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepositoryIndexingService {

    private final RepositoryService repositoryService;

    private final UserService userService;

    private final GitHubRepositoryClient githubRepositoryClient;

    private final CodeFileFilter codeFileFilter;

    private final CodeChunker codeChunker;

    private final CodeChunkRepository codeChunkRepository;

    private final VectorStoreService vectorStoreService;

    @Transactional
    public List<CodeChunk> indexRepository(
            UUID repositoryId,
            UUID userId) {

        Repository repository =
                repositoryService.getRepository(
                        repositoryId,
                        userId
                );

        User user =
                userService.requiredById(userId);

        String accessToken =
                userService.decryptAccessToken(user);

        repository.setIndexStatus(
                IndexStatus.INDEXING
        );

        repository.setErrorMessage(null);

        repository.setFilesProcessed(0);

        repository.setFilesTotal(0);

        repository.setChunkCount(0);

        try {

            /*
             * 1. Get repository tree from GitHub.
             */
            GitHubTreeResponse tree =
                    githubRepositoryClient.getRepositoryTree(
                            accessToken,
                            repository.getOwner(),
                            repository.getName(),
                            repository.getDefaultBranch()
                    );

            /*
             * 2. Find supported source files.
             */
            List<GitHubTreeResponse.TreeItem> files =
                    tree.tree()
                            .stream()
                            .filter(item ->
                                    "blob".equals(item.type())
                            )
                            .filter(item ->
                                    codeFileFilter.isSupported(
                                            item.path(),
                                            item.size()
                                    )
                            )
                            .toList();

            repository.setFilesTotal(
                    files.size()
            );

            /*
             * 3. Remove previous code chunks.
             */
            codeChunkRepository.deleteByRepositoryId(
                    repositoryId
            );

            List<CodeChunk> allChunks =
                    new ArrayList<>();

            /*
             * 4. Download and chunk every file.
             */
            for (GitHubTreeResponse.TreeItem file : files) {

                GitHubFileResponse response =
                        githubRepositoryClient.getFile(
                                accessToken,
                                repository.getOwner(),
                                repository.getName(),
                                file.path(),
                                repository.getDefaultBranch()
                        );

                if (response == null ||
                        response.content() == null) {

                    continue;
                }

                /*
                 * 5. Decode GitHub Base64 content.
                 */
                String content =
                        decodeContent(
                                response.content()
                        );

                /*
                 * 6. Split source code into chunks.
                 */
                List<CodeChunk> chunks =
                        codeChunker.chunk(
                                file.path(),
                                content
                        );

                /*
                 * 7. Convert DTOs into database entities.
                 */
                List<devpilot.backend.entity.CodeChunk> entities =
                        chunks.stream()
                                .map(chunk ->
                                        devpilot.backend.entity.CodeChunk
                                                .builder()
                                                .repositoryId(
                                                        repositoryId
                                                )
                                                .filePath(
                                                        chunk.filePath()
                                                )
                                                .chunkIndex(
                                                        chunk.chunkIndex()
                                                )
                                                .content(
                                                        chunk.content()
                                                )
                                                .build()
                                )
                                .toList();

                /*
                 * 8. Save chunks.
                 */
                codeChunkRepository.saveAll(
                        entities
                );

                allChunks.addAll(
                        chunks
                );

                repository.setFilesProcessed(
                        repository.getFilesProcessed() + 1
                );

                repository.setChunkCount(
                        allChunks.size()
                );
            }

            /*
             * 9. Generate embeddings.
             *
             * VectorStore will use the configured
             * Spring AI EmbeddingModel.
             */
            vectorStoreService.indexRepository(
                    repositoryId
            );

            /*
             * 10. Indexing completed.
             */
            repository.setIndexStatus(
                    IndexStatus.COMPLETED
            );

            repository.setIndexedAt(
                    Instant.now()
            );

            return allChunks;

        } catch (Exception exception) {

            repository.setIndexStatus(
                    IndexStatus.FAILED
            );

            repository.setErrorMessage(
                    exception.getMessage()
            );

            throw exception;
        }
    }

    private String decodeContent(String content) {

        String normalizedContent =
                content.replaceAll(
                        "\\s",
                        ""
                );

        byte[] decoded =
                Base64.getDecoder().decode(
                        normalizedContent
                );

        return new String(
                decoded,
                StandardCharsets.UTF_8
        );
    }
}