package ecole._2.jobs.entities;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String responsable;

    @Column(nullable = false)
    private String poste;

    @Column
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column
    private String adresse;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true)
    private String login; // Automatically generated login

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

    // Generate a logical login based on company name
    private String generateLogin(String name) {
        if (name == null) name = "company";
        String base = name.toLowerCase().replaceAll("[^a-z0-9]", ""); // remove spaces/special chars
        long timestamp = System.currentTimeMillis() % 1000; // simple number to reduce duplicates
        return base + String.format("%03d", timestamp);
    }
}
