package ecole._2.jobs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import ecole._2.jobs.entities.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Méthodes CRUD automatiques

    // Find a company by its login
    Optional<Company> findByLogin(String login);
}
