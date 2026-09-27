package devpilot.backend.controller;

import java.util.List;
import java.util.UUID;

import devpilot.backend.dto.CodeChunk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import devpilot.backend.dto.RepositoryResponse;
import devpilot.backend.security.AppUserPrincipal;
import devpilot.backend.security.CurrentUser;
import devpilot.backend.service.RepositoryIndexingService;
import devpilot.backend.service.RepositoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {

    private final CurrentUser currentUser;
    private final RepositoryService repositoryService;
    private final RepositoryIndexingService repositoryIndexingService;

    @PostMapping("/sync")
    public ResponseEntity<List<RepositoryResponse>> syncRepositories() {

        AppUserPrincipal principal =
                currentUser.require();

        UUID userId =
                principal.getUser().getId();

        return ResponseEntity.ok(
                repositoryService.syncRepositories(
                        userId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<RepositoryResponse>> getRepositories() {

        AppUserPrincipal principal =
                currentUser.require();

        UUID userId =
                principal.getUser().getId();

        return ResponseEntity.ok(
                repositoryService.getRepositories(
                        userId
                )
        );
    }

    @PostMapping("/{repositoryId}/index")
    public ResponseEntity<List<CodeChunk>> indexRepository(
            @PathVariable UUID repositoryId) {

        AppUserPrincipal principal =
                currentUser.require();

        UUID userId =
                principal.getUser().getId();

        return ResponseEntity.ok(
                repositoryIndexingService.indexRepository(
                        repositoryId,
                        userId
                )
        );
    }
}

