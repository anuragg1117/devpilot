package devpilot.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import devpilot.backend.exceptions.UnauthorizedException;

@Component
public class CurrentUser {

    public AppUserPrincipal require() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {

            throw new UnauthorizedException("Not authenticated");
        }

        return principal;
    }
}