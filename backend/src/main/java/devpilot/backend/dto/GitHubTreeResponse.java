package devpilot.backend.dto;

import java.util.List;

public record GitHubTreeResponse(
        String sha,
        String url,
        boolean truncated,
        List<TreeItem> tree
) {

    public record TreeItem(
            String path,
            String mode,
            String type,
            String sha,
            Integer size,
            String url
    ) {
    }
}