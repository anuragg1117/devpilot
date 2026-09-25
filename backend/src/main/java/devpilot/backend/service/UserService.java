package devpilot.backend.service;

import devpilot.backend.entity.User;
import devpilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByGithubId(Long githubId) {
        return userRepository.findByGithubId(githubId);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}