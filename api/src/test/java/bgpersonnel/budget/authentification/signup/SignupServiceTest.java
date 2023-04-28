package bgpersonnel.budget.authentification.signup;

import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.repository.RoleRepository;
import bgpersonnel.budget.authentification.common.repository.UserRepository;
import bgpersonnel.budget.authentification.security.JwtUtils;
import bgpersonnel.budget.exeception.AlreadyExistsException;
import bgpersonnel.budget.exeception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private SignupService signupService;

    private SignupRequest signUpRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        signupService = new SignupService(userRepository, roleRepository, encoder, jwtUtils);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        signUpRequest = new SignupRequest();
        signUpRequest.setName("John");
        signUpRequest.setEmail("john@example.com");
        signUpRequest.setPassword("password");
    }

    @DisplayName("if user with the same email does not exist, save the user")
    @Test
    void test_register_user_with_valid_data_Success() {
        //given
        signUpRequest.setRole(Collections.singleton("user"));
        //when
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any(ERole.class))).thenReturn(Optional.of(new Role(1, ERole.ROLE_USER)));
        //then
        assertDoesNotThrow(() -> signupService.registerUser(signUpRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @DisplayName("if user with the same email already exists, throw AlreadyExistsException")
    @Test
    void test_register_user_with_existing_email_failed() {
        //given
        signUpRequest.setRole(Collections.singleton("user"));
        //when
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        //then
        assertThrows(AlreadyExistsException.class, () -> signupService.registerUser(signUpRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @DisplayName("user with role admin can be registered")
    @Test
    void test_register_user_with_valide_role_admin_success() {
        //given
        signUpRequest.setRole(Collections.singleton("admin"));
        //when
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any(ERole.class))).thenReturn(Optional.of(new Role(1, ERole.ROLE_ADMIN)));
        //then
        assertDoesNotThrow(() -> signupService.registerUser(signUpRequest));
        verify(userRepository, times(1)).save(any(User.class));

    }

    @DisplayName("user with role roleNotExists cannot be registered")
    @Test
    void test_register_user_with_invalid_role_failed() {
        //given
        signUpRequest.setRole(Collections.singleton("roleNotExists"));
        //when
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any(ERole.class))).thenReturn(Optional.empty());
        //then
        assertThrows(NotFoundException.class, () -> signupService.registerUser(signUpRequest));

    }

    @DisplayName("user without role can be registered")
    @Test
    void test_register_user_without_role_success() {
        //when
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any(ERole.class))).thenReturn(Optional.of(new Role(1, ERole.ROLE_USER)));
        //then
        assertDoesNotThrow(() -> signupService.registerUser(signUpRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }
}