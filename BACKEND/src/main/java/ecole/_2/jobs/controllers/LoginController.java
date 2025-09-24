package ecole._2.jobs.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ecole._2.jobs.config.JwtProperties;
import ecole._2.jobs.dto.ApiResponse;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    private final JwtProperties jwtProperties;
    private final String RESPONSE_TYPE = "code";
    private final String SCOPE = "public projects profile tig elearning forum";
    private final String STATE = "123456";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginController(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /** Step 1: Redirect user to 42 OAuth login */
    @GetMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirectUri = jwtProperties.getRedirect_uri();
        String authUrl = "https://api.intra.42.fr/oauth/authorize" +
                "?client_id=" + jwtProperties.getClient_id() +
                "&redirect_uri=" + redirectUri +
                "&response_type=" + RESPONSE_TYPE +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8) +
                "&state=" + STATE;

        response.sendRedirect(authUrl);
    }

    /** Step 2: Handle callback and exchange "code" for access_token */
    @GetMapping("/auth")
    public void callback(String code, HttpServletResponse response) throws Exception {
        if (code == null || code.isEmpty()) {
            response.sendRedirect("http://localhost:5173/login?error=Missing+authorization+code");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code" +
                "&client_id=" + jwtProperties.getClient_id() +
                "&client_secret=" + jwtProperties.getClient_secret() +
                "&code=" + code +
                "&redirect_uri=" + URLEncoder.encode(jwtProperties.getRedirect_uri(), StandardCharsets.UTF_8);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> tokenResp = restTemplate.exchange(
                "https://api.intra.42.fr/oauth/token",
                HttpMethod.POST,
                entity,
                String.class
        );

        if (tokenResp.getStatusCode() != HttpStatus.OK) {
            response.sendRedirect("http://localhost:5173/login?error=Failed+to+fetch+token");
            return;
        }

        JsonNode tokenJson = objectMapper.readTree(tokenResp.getBody());
        String accessToken = tokenJson.path("access_token").asText();

        String dashboardUrl = "http://localhost:5173/dashboard?accessToken=" 
                            + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        response.sendRedirect(dashboardUrl);
    }


    /** Step 3: Verify token with /oauth/token/info */
    @GetMapping("/verify")
    public ApiResponse<String> verifyToken(String accessToken) {
        try {
            if (accessToken == null || accessToken.isEmpty()) {
                return new ApiResponse<>(false, null, "Missing access token");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(
                    "https://api.intra.42.fr/oauth/token/info",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (resp.getStatusCode() != HttpStatus.OK) {
                return new ApiResponse<>(false, null, "Invalid token");
            }

            JsonNode json = objectMapper.readTree(resp.getBody());
            String uid = json.path("application").path("uid").asText();

            if (jwtProperties.getClient_id().equals(uid)) {
                return new ApiResponse<>(true, uid, "✅ Token valid for this app");
            } else {
                return new ApiResponse<>(false, uid, "❌ Token does not match client_id");
            }

        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/")
    public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        login(request, response);
    }
}
