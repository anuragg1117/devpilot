package devpilot.backend.security;

import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import devpilot.backend.entity.User;
import devpilot.backend.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubOAuth2UserService
        implements OAuth2UserService<
        OAuth2UserRequest,
        OAuth2User> {

    private final UserService userService;

    private final DefaultOAuth2UserService delegate =
            new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest) {

        OAuth2User githubUser =
                delegate.loadUser(userRequest);

        Map<String, Object> attributes =
                githubUser.getAttributes();

        String accessToken =
                userRequest
                        .getAccessToken()
                        .getTokenValue();

        String scopes =
                String.join(
                        ",",
                        userRequest
                                .getAccessToken()
                                .getScopes());

        User user =
                userService.upsertFromGitHub(
                        attributes,
                        accessToken,
                        scopes);

        attributes.put(
                "userId",
                user.getId().toString());

        return new DefaultOAuth2User(
                java.util.List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_USER")),
                attributes,
                "id");
    }
}