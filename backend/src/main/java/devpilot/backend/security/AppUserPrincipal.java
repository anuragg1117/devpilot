package devpilot.backend.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import devpilot.backend.entity.User;

public class AppUserPrincipal implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public AppUserPrincipal(
            User user,
            Map<String, Object> attributes) {

        this.user = user;
        this.attributes = attributes;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    @Override
    public String getName() {
        return user.getGithubUsername();
    }
}