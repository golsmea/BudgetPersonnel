package bgpersonnel.budget.authentification.signin;

import bgpersonnel.budget.authentification.refreshtoken.RefreshToken;
import bgpersonnel.budget.authentification.refreshtoken.RefreshTokenService;
import bgpersonnel.budget.authentification.security.JwtUtils;
import bgpersonnel.budget.authentification.security.services.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SigninService {

    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    RefreshTokenService refreshTokenService;

    public SigninService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    public JwtResponse signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getName(), userDetails.getEmail(), roles);
    }

}
