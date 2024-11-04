package al.project.shorturl.shorturlgenerator.configuration;

import al.project.shorturl.shorturlgenerator.entity.User;
import al.project.shorturl.shorturlgenerator.security.AuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationService authenticationService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth",           // Auth endpoints
            "/swagger-ui",         // Swagger UI
            "/swagger-ui.html",
            "/v3/api-docs",        // OpenAPI docs
            "/h2-console"          // H2 Console
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");
        try {
            String username = authenticationService.validateTokenAndGetUsername(token);
            if (username != null) {
                User authUser = new User(username, "", new ArrayList<>());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authUser, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

}