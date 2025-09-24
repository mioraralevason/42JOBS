package ecole._2.jobs.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long companyId;

    @Column(nullable = false)
    private LocalDateTime deactivateAt; // date of deactivation
}
