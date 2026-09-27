package devpilot.backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * GitHub's unique user ID.
     *
     * This is the primary external identifier used to
     * find an existing DevPilot user during OAuth login.
     */
    @Column(
            name = "github_id",
            nullable = false,
            unique = true
    )
    private Long githubId;

    /**
     * GitHub username/login.
     */
    @Column(
            name = "github_username",
            nullable = false,
            length = 100
    )
    private String githubUsername;

    /**
     * GitHub display name.
     *
     * Falls back to githubUsername when GitHub does not
     * provide a display name.
     */
    @Column(
            name = "display_name",
            nullable = false,
            length = 200
    )
    private String displayName;

    /**
     * GitHub avatar URL.
     */
    @Column(
            name = "avatar_url",
            length = 500
    )
    private String avatarUrl;

    /**
     * Encrypted GitHub OAuth access token.
     *
     * NEVER expose this field through an API response.
     * UserService encrypts the token before persistence.
     */
    @Column(
            name = "access_token",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String accessToken;

    /**
     * OAuth scopes granted by GitHub.
     *
     * Example:
     * read:user,user:email,repo
     */
    @Column(
            name = "token_scopes",
            length = 500
    )
    private String tokenScopes;

    /**
     * Time when the DevPilot user was created.
     */
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}