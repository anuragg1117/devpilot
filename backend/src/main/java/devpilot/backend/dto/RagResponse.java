package devpilot.backend.dto;

import java.util.List;

public record RagResponse(
        String answer,
        List<Citation> citations) {

    public record Citation(
            String filePath,
            Integer chunkIndex) {
    }
}