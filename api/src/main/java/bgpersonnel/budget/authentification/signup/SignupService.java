package bgpersonnel.budget.authentification.signup;

import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.repository.RoleRepository;
import bgpersonnel.budget.authentification.common.repository.UserRepository;
import bgpersonnel.budget.authentification.security.JwtUtils;
import bgpersonnel.budget.exeception.AlreadyExistsException;
import bgpersonnel.budget.exeception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class SignupService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder encoder;
    JwtUtils jwtUtils;

    public SignupService(UserRepository userRepository, RoleRepository roleRepository,
                         PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }


    // optimize this method
    public void registerUser(SignupRequest signUpRequest) {
        validateUsernameAndPassword(signUpRequest);
        // Create new user's account
        User user = new User(signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = getRoles(signUpRequest);

        user.setRoles(roles);
        userRepository.save(user);
    }

    private Set<Role> getRoles(SignupRequest signUpRequest) {
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(findRoleByNameOrThrow(ERole.ROLE_USER));
            return roles;
        }

        strRoles.forEach(role -> {
            if (role.equals("admin")) {
                roles.add(findRoleByNameOrThrow(ERole.ROLE_ADMIN));
            } else {
                roles.add(findRoleByNameOrThrow(ERole.ROLE_USER));
            }
        });

        return roles;
    }

    private void validateUsernameAndPassword(SignupRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new AlreadyExistsException("Error: Email is already in use!");
        }
    }


    private Role findRoleByNameOrThrow(ERole roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
    }

}
