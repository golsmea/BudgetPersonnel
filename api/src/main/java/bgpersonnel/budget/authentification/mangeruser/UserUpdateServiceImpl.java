package bgpersonnel.budget.authentification.mangeruser;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserService userService;

    private final PasswordEncoder encoder;

    public UserUpdateServiceImpl(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    /**
     * Update user information (name, email)
     */
    @Override
    public void updateUserInfo(UserUpdateRequest userUpdateRequest) {

        User user = this.userService.getConnectedUser();
        if (userUpdateRequest.getName() != null) {
            user.setName(userUpdateRequest.getName());
        }
        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        this.userService.saveUser(user);
    }

    /**
     * Update user password
     */
    @Override
    public void updatePassword(UpdatedPassword updatedPassword) {

        User user = this.userService.getConnectedUser();

        // Check if the old password provided by the user matches the stored password
        if (!encoder.matches(updatedPassword.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }

        // Encode the new password
        String newEncodedPassword = this.encoder.encode(updatedPassword.getNewPassword());
        user.setPassword(newEncodedPassword);

        // Save the updated user with the new password
        this.userService.saveUser(user);
    }

}

