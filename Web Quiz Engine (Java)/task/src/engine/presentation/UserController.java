package engine.presentation;

import engine.business.models.User;
import engine.business.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final PasswordEncoder encoder;

    @PostMapping("/api/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User user) {
        if (userService.existsByEmailIgnoreCase(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
            userService.register(user);
            return ResponseEntity.ok().build();
        }
    }
}