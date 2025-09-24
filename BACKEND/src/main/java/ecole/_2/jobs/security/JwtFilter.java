package ecole._2.jobs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Jwts;
import ecole._2.jobs.config.JwtProperties;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        // if ("POST".equalsIgnoreCase(method) &&
        //     ("/jobs/companies/activate".equals(path) || "/jobs/companies/deactivate".equals(path))) {

        //     String authHeader = request.getHeader("Authorization");

        //     if (authHeader != null && authHeader.startsWith("Bearer ")) {
        //         String token = authHeader.substring(7);
        //         try {
        //             // Validate JWT token using client_secret
        //             Jwts.parser().setSigningKey(jwtProperties.getClient_secret()).parseClaimsJws(token);
        //         } catch (Exception e) {
        //             sendJsonError(response, "Invalid JWT token");
        //             return;
        //         }
        //     } else {
        //         sendJsonError(response, "Missing Authorization header");
        //         return;
        //     }
        // }

        filterChain.doFilter(request, response);
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"status\":false,\"data\":null,\"message\":\"" + message + "\"}");
        writer.flush();
    }
}
