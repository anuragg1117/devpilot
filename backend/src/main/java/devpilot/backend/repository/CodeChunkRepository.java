package devpilot.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devpilot.backend.entity.CodeChunk;

public interface CodeChunkRepository extends JpaRepository<CodeChunk, UUID> {

    List<CodeChunk> findByRepositoryIdOrderByFilePathAscChunkIndexAsc(
            UUID repositoryId
    );

    void deleteByRepositoryId(UUID repositoryId);

    long countByRepositoryId(UUID repositoryId);
}