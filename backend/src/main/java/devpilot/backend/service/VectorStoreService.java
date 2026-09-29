package devpilot.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devpilot.backend.entity.CodeChunk;
import devpilot.backend.repository.CodeChunkRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final CodeChunkRepository codeChunkRepository;

    private final VectorStore vectorStore;

    @Transactional(readOnly = true)
    public void indexRepository(UUID repositoryId) {

        List<CodeChunk> chunks =
                codeChunkRepository
                        .findByRepositoryIdOrderByFilePathAscChunkIndexAsc(
                                repositoryId
                        );

        if (chunks.isEmpty()) {
            return;
        }

        List<Document> documents =
                chunks.stream()
                        .map(this::toDocument)
                        .toList();



        vectorStore.add(documents);
    }

    private Document toDocument(CodeChunk chunk) {

        return Document.builder()
                .text(chunk.getContent())
                .metadata(
                        "repositoryId",
                        chunk.getRepositoryId().toString()
                )
                .metadata(
                        "filePath",
                        chunk.getFilePath()
                )
                .metadata(
                        "chunkIndex",
                        chunk.getChunkIndex()
                )
                .metadata(
                        "codeChunkId",
                        chunk.getId().toString()
                )
                .build();
    }
}