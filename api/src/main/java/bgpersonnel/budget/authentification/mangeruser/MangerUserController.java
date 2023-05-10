package bgpersonnel.budget.authentification.mangeruser;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class MangerUserController {

    @Autowired
    private UserUpdateService userUpdateService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update-user")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequest userUpdateDto) {
        userUpdateService.updateUserInfo(userUpdateDto);
        return ResponseEntity.ok("User information updated successfully!");
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatedPassword password) {
        userUpdateService.updatePassword(password);
        return ResponseEntity.ok("Password updated successfully!");
    }

}
