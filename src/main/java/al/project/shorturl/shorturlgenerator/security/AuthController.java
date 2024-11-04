package al.project.shorturl.shorturlgenerator.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "User Login",
            description = "Authenticate a user and return a JWT token for accessing secured endpoints.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated and returned a JWT token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String token = authenticationService.authenticateUser(username, password);
        return ResponseEntity.ok(token);
    }
}
