package bgpersonnel.budget.authentification.refreshtoken;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.repository.UserRepository;
import bgpersonnel.budget.authentification.security.JwtUtils;
import bgpersonnel.budget.authentification.security.exeception.TokenRefreshException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 60000L);

    }

    @DisplayName("find refresh token by token")
    @Test
    void testFindByToken() {
        String token = "testToken";
        RefreshToken refreshToken = new RefreshToken();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);
        verify(refreshTokenRepository, times(1)).findByToken(token);
        assertEquals(Optional.of(refreshToken), result);
    }

    @DisplayName("create refresh token")
    @Test
    void testCreateRefreshToken() {
        Long userId = 1L;
        RefreshToken refreshToken = new RefreshToken();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        RefreshToken result = refreshTokenService.createRefreshToken(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        assertNotNull(result);
    }

    @DisplayName("check if refresh not expired")
    @Test
    void testVerifyExpiration_ValidToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(Instant.now().plusMillis(1000L)); // set expiry date in future
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);
        assertEquals(refreshToken, result);
    }

    @DisplayName("check if refresh token expired")
    @Test
    void testVerifyExpiration_ExpiredToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000L)); // set expiry date in past
        assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    @Test
    void testRefreshToken_ValidToken() {
        String refreshToken = "testRefreshToken";
        User user = new User();
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(100000L)); // set expiry date in future
        token.setUser(user);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(token);
        when(jwtUtils.generateTokenFromUsername(user.getEmail())).thenReturn("testToken");
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(refreshToken);
        TokenRefreshResponse result = refreshTokenService.refreshToken(request);

        verify(refreshTokenRepository, times(1)).findByToken(refreshToken);
        verify(jwtUtils, times(1)).
                generateTokenFromUsername(user.getEmail());

        assertNotNull(result);
        assertEquals("testToken", result.getAccessToken());
    }

    @Test
    void testRefreshToken_ExpiredToken() {
        String refreshToken = "testRefreshToken";
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusMillis(1000L)); // set expiry date in past
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(token));
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(refreshToken);
        assertThrows(TokenRefreshException.class, () -> refreshTokenService.refreshToken(request));
        verify(refreshTokenRepository, times(1)).findByToken(refreshToken);
        verify(refreshTokenRepository, times(1)).delete(token);
    }

}