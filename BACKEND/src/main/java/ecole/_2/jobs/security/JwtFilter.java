package ecole._2.jobs.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecole._2.jobs.config.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) &&
             ("/jobs/companies/activate".equals(path) || "/jobs/companies/deactivate".equals(path))) {

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (!isTokenValid(token)) {
                    sendJsonError(response, "Invalid JWT token");
                    return;
                }

            } else {
                sendJsonError(response, "Missing Authorization header");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenValid(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(
                    "https://api.intra.42.fr/oauth/token/info",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (resp.getStatusCode() != HttpStatus.OK) {
                return false;
            }

            JsonNode json = objectMapper.readTree(resp.getBody());
            String uid = json.path("application").path("uid").asText();

            return jwtProperties.getClient_id().equals(uid);

        } catch (Exception e) {
            return false;
        }
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"status\":false,\"data\":null,\"message\":\"" + message + "\"}");
        writer.flush();
    }
}
