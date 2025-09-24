package ecole._2.jobs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Responsable is required")
    @Column(nullable = false)
    private String responsable;

    @NotBlank(message = "Poste is required")
    @Column(nullable = false)
    private String poste;

    @NotBlank(message = "Telephone is required")
    @Column(nullable = false)
    private String telephone;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Adresse is required")
    @Column(nullable = false)
    private String adresse;

    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true)
    private String login;

    @Column
    private LocalDateTime validateAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.password = hashPassword(this.password);
        this.login = generateLogin(this.name); // generate login automatically
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String hashPassword(String rawPassword) {
        if (rawPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private String generateLogin(String name) {
        if (name == null) name = "company";
        String base = name.toLowerCase().replaceAll("[^a-z0-9]", "");
        long timestamp = System.currentTimeMillis() % 1000;
        return base + String.format("%03d", timestamp);
    }
}
