package devpilot.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devpilot.backend.dto.RepositoryResponse;
import devpilot.backend.security.AppUserPrincipal;
import devpilot.backend.security.CurrentUser;
import devpilot.backend.service.RepositoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {

    private final CurrentUser currentUser;
    private final RepositoryService repositoryService;

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
}