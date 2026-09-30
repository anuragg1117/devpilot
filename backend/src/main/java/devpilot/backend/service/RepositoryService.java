package devpilot.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devpilot.backend.dto.GitHubRepositoryResponse;
import devpilot.backend.dto.RepositoryResponse;
import devpilot.backend.entity.Repository;
import devpilot.backend.entity.User;
import devpilot.backend.exceptions.ResourceAccessDeniedException;
import devpilot.backend.exceptions.ResourceNotFoundException;
import devpilot.backend.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final UserService userService;
    private final GitHubRepositoryClient githubRepositoryClient;

    @Transactional
    public List<RepositoryResponse> syncRepositories(
            UUID userId) {

        User user = userService.requiredById(userId);

        String accessToken =
                userService.decryptAccessToken(user);

        List<GitHubRepositoryResponse> githubRepositories =
                githubRepositoryClient.getRepositories(
                        accessToken
                );

        List<Repository> repositories =
                githubRepositories.stream()
                        .map(repository ->
                                upsertRepository(
                                        userId,
                                        repository
                                ))
                        .toList();

        return repositoryRepository
                .saveAll(repositories)
                .stream()
                .map(RepositoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RepositoryResponse> getRepositories(
            UUID userId) {

        return repositoryRepository
                .findByUserIdOrderByFullNameAsc(userId)
                .stream()
                .map(RepositoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Repository getRepository(
            UUID repositoryId,
            UUID userId) {

        /*
         * First determine whether the repository exists.
         */
        Repository repository =
                repositoryRepository
                        .findById(repositoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Repository not found"
                                ));

        /*
         * The repository exists, but check ownership.
         */
        if (!repository.getUserId().equals(userId)) {
            throw new ResourceAccessDeniedException(
                    "You do not have access to this repository"
            );
        }

        return repository;
    }

    private Repository upsertRepository(
            UUID userId,
            GitHubRepositoryResponse githubRepository) {

        Repository repository =
                repositoryRepository
                        .findByUserIdAndGithubRepoId(
                                userId,
                                githubRepository.id()
                        )
                        .orElseGet(Repository::new);

        repository.setUserId(userId);

        repository.setGithubRepoId(
                githubRepository.id()
        );

        repository.setOwner(
                githubRepository.owner().login()
        );

        repository.setName(
                githubRepository.name()
        );

        repository.setFullName(
                githubRepository.fullName()
        );

        repository.setPrivate(
                githubRepository.isPrivate()
        );

        repository.setDefaultBranch(
                githubRepository.defaultBranch()
        );

        repository.setLanguage(
                githubRepository.language()
        );

        repository.setHtmlUrl(
                githubRepository.htmlUrl()
        );

        repository.setDescription(
                githubRepository.description()
        );

        return repository;
    }
}