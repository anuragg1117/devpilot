package devpilot.backend.dto;

public record CodeFile(
        String path,
        String content,
        int size
) {
}