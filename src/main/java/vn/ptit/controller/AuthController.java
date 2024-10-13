package vn.ptit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.ptit.dto.request.UserRequest;
import vn.ptit.dto.response.ResponseJSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@SuppressWarnings("unused")
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest,
                                   HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /* Set user info in session when authenticated success */
        HttpSession session = request.getSession(true);
        session.setAttribute("isAuthenticated", true);
        session.setAttribute("username", userRequest.getUsername());

        return ResponseJSON.ok("Login success!");
    }

    @PostMapping(value = "/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);

            /* Check authentication state in session */
            if (session != null && (Boolean) session.getAttribute("isAuthenticated")) {
                /* Cancel authentication info */
                SecurityContextHolder.getContext().setAuthentication(null);
                session.invalidate();

                return ResponseJSON.ok("Logout success!");
            }

            return ResponseJSON.badRequest("You was not logged in!");
        } catch (Exception e) {
            return ResponseJSON.badRequest("Logout error: " + e.getMessage());
        }
    }
}
