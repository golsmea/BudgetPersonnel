package bgpersonnel.budget.authentification.signup;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class SignupController {

    SignupService signUpService;

    public SignupController(SignupService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        signUpService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}