package bgpersonnel.budget.authentification.refreshtoken;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(request));
    }
}