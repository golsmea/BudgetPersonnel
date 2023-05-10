package bgpersonnel.budget.authentification.signin;

import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.refreshtoken.RefreshToken;
import bgpersonnel.budget.authentification.refreshtoken.RefreshTokenService;
import bgpersonnel.budget.authentification.security.JwtUtils;
import bgpersonnel.budget.authentification.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SigninServiceTest {

    @InjectMocks
    private SigninService signinService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenService refreshTokenService;

    // Fixture - Create a User object for testing
    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRoles(Set.of(new Role(1, ERole.ROLE_USER)));
        return user;
    }

    @BeforeEach
    void setUp() {
        // Reset SecurityContextHolder before each test
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    void testSignIn() {
        // Mocking any dependencies or setup necessary for.
        // Test case setup
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        User user = createUser();

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        String jwtToken = "someJwtToken";
        Mockito.when(jwtUtils.generateJwtToken(Mockito.any(UserDetailsImpl.class))).thenReturn(jwtToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("someRefreshToken");
        Mockito.when(refreshTokenService.createRefreshToken(Mockito.anyLong())).thenReturn(refreshToken);

        // Test case execution
        JwtResponse jwtResponse = signinService.signIn(loginRequest);

        // verifications
        assertNotNull(jwtResponse);
        assertEquals(jwtToken, jwtResponse.getToken());
        assertEquals("someRefreshToken", jwtResponse.getRefreshToken());
        assertEquals(1L, jwtResponse.getId());
        assertEquals("Test User", jwtResponse.getName());
        assertEquals("test@example.com", jwtResponse.getEmail());
        assertEquals(List.of("ROLE_USER"), jwtResponse.getRoles());
    }

    // Other test methods for different scenarios

}