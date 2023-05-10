package bgpersonnel.budget.authentification.common.services;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.repository.UserRepository;
import bgpersonnel.budget.authentification.security.services.UserDetailsImpl;
import bgpersonnel.budget.exeception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static Long getIdConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // get Connected user
    public User getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return findUserByEmail(userDetails.getEmail());
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
