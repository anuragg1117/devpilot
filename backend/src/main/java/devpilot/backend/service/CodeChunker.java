package devpilot.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import devpilot.backend.dto.CodeChunk;

@Component
public class CodeChunker {

    private final int chunkSize;
    private final int chunkOverlap;

    public CodeChunker(
            @Value("${devpilot.indexing.chunk-size}") int chunkSize,
            @Value("${devpilot.indexing.chunk-overlap}") int chunkOverlap) {

        if (chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException(
                    "Chunk overlap must be smaller than chunk size"
            );
        }

        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    public List<CodeChunk> chunk(String filePath, String content) {

        List<CodeChunk> chunks = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return chunks;
        }

        int start = 0;
        int chunkIndex = 0;

        while (start < content.length()) {

            int end = Math.min(
                    start + chunkSize,
                    content.length()
            );

            String chunkContent = content.substring(start, end);

            chunks.add(
                    new CodeChunk(
                            filePath,
                            chunkIndex++,
                            chunkContent
                    )
            );

            if (end == content.length()) {
                break;
            }

            start = end - chunkOverlap;
        }

        return chunks;
    }
}