package ecole._2.jobs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String client_id;
    private String client_secret;
    private String redirect_uri;

    // Getters & Setters
    public String getClient_id() { return client_id; }
    public void setClient_id(String client_id) { this.client_id = client_id; }

    public String getClient_secret() { return client_secret; }
    public void setClient_secret(String client_secret) { this.client_secret = client_secret; }

    public String getRedirect_uri() { return redirect_uri; }
    public void setRedirect_uri(String redirect_uri) { this.redirect_uri = redirect_uri; }
}
