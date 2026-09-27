package devpilot.backend.service;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class CodeFileFilter {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            ".java",
            ".js",
            ".jsx",
            ".ts",
            ".tsx",
            ".py",
            ".go",
            ".cpp",
            ".c",
            ".h",
            ".html",
            ".css",
            ".scss",
            ".sql",
            ".xml",
            ".json",
            ".yaml",
            ".yml",
            ".properties",
            ".md"
    );

    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            ".git",
            ".idea",
            ".vscode",
            "node_modules",
            "target",
            "build",
            "dist",
            "out"
    );

    public boolean isSupported(String path, Integer size) {

        if (size == null || size <= 0) {
            return false;
        }

        if (size > 1_000_000) {
            return false;
        }

        String normalizedPath = path.toLowerCase();

        for (String directory : IGNORED_DIRECTORIES) {
            if (normalizedPath.contains("/" + directory + "/")
                    || normalizedPath.startsWith(directory + "/")) {
                return false;
            }
        }

        return SUPPORTED_EXTENSIONS.stream()
                .anyMatch(normalizedPath::endsWith);
    }
}