package devpilot.backend.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devpilot.backend.entity.User;
import devpilot.backend.exceptions.ResourceNotFoundException;
import devpilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextEncryptor tokenEncryptor;

    /**
     * Creates a new DevPilot user or updates an existing user
     * using the information received from GitHub OAuth.
     */
    @Transactional
    public User upsertFromGitHub(
            Map<String, Object> attributes,
            String accessToken,
            String scopes) {

        Long githubId = toLong(attributes.get("id"));

        String login = String.valueOf(
                attributes.get("login")
        );

        String displayName = attributes.get("name") != null
                ? String.valueOf(attributes.get("name"))
                : login;

        String avatarUrl = attributes.get("avatar_url") != null
                ? String.valueOf(attributes.get("avatar_url"))
                : null;

        /*
         * Never store the GitHub access token as plain text.
         */
        String encryptedToken =
                tokenEncryptor.encrypt(accessToken);

        /*
         * Find the existing DevPilot user using the
         * unique GitHub ID. If the user doesn't exist,
         * create a new User entity.
         */
        User user = userRepository
                .findByGithubId(githubId)
                .orElseGet(User::new);

        user.setGithubId(githubId);
        user.setGithubUsername(login);
        user.setDisplayName(displayName);
        user.setAvatarUrl(avatarUrl);
        user.setAccessToken(encryptedToken);
        user.setTokenScopes(scopes);

        return userRepository.save(user);
    }

    /**
     * Returns a user by their DevPilot UUID.
     */
    @Transactional(readOnly = true)
    public User requiredById(UUID id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id
                        )
                );
    }

    /**
     * Decrypts the stored GitHub access token.
     *
     * This should only be used by backend services that
     * need to communicate with GitHub on behalf of the user.
     */
    public String decryptAccessToken(User user) {

        return tokenEncryptor.decrypt(
                user.getAccessToken()
        );
    }

    /**
     * Converts the GitHub ID returned by OAuth
     * into a Long regardless of whether the provider
     * returns it as a Number or String.
     */
    private static Long toLong(Object value) {

        if (value == null) {
            throw new IllegalArgumentException(
                    "GitHub user ID is missing"
            );
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.parseLong(
                String.valueOf(value)
        );
    }
}