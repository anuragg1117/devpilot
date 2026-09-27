package devpilot.backend.dto;

public record CodeChunk(
        String filePath,
        int chunkIndex,
        String content
) {
}