package ecole._2.jobs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ecole._2.jobs.entities.HistoriqueAccount;

@Repository
public interface HistoriqueAccountRepository extends JpaRepository<HistoriqueAccount, Long> {
}
