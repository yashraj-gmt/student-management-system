package com.app.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");

        // You can check exception type to customize message or error parameter
        String errorParam = "unknown";

        if (email != null && !email.isEmpty()) {
            // Here you would typically check if email exists in DB or not.
            // For example, you can inject UserService here or check in DB directly
            // but for demo, assume email exists, so password is wrong
            // If you want full check, inject UserService and do:
            // if (userService.findByEmail(email) == null) { errorParam = "user-not-found"; } else { errorParam = "wrong-password"; }

            // For demo, let's just set errorParam to wrong-password for now:
            errorParam = "wrong-password";
        } else {
            errorParam = "user-not-found";
        }

        // Redirect with error and email param
        response.sendRedirect("/login?error=" + errorParam + "&email=" + email);
    }
}
