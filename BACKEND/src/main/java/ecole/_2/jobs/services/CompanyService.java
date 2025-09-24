package ecole._2.jobs.services;

import ecole._2.jobs.entities.Company;
import ecole._2.jobs.entities.HistoriqueAccount;
import ecole._2.jobs.repositories.CompanyRepository;
import ecole._2.jobs.repositories.HistoriqueAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository repo;
    private final HistoriqueAccountRepository historiqueRepo;

    public CompanyService(CompanyRepository repo, HistoriqueAccountRepository historiqueRepo) {
        this.repo = repo;
        this.historiqueRepo = historiqueRepo;
    }

    public List<Company> getAllCompanies() {
        return repo.findAll();
    }

    public Optional<Company> getCompanyById(Long id) {
        return repo.findById(id);
    }

    public Optional<Company> getCompanyByLogin(String login) {
        return repo.findByLogin(login);
    }

    public Company saveCompany(Company company) {
        return repo.save(company);
    }

    public void deleteCompany(Long id) {
        repo.deleteById(id);
    }
    public boolean activateCompany(String login) {
        Optional<Company> opt = repo.findByLogin(login);
        if (opt.isPresent()) {
            Company company = opt.get();
            company.setValidateAt(LocalDateTime.now());
            repo.save(company);
            return true;
        }
        return false;
    }
    public boolean deactivateCompany(String login) {
        Optional<Company> opt = repo.findByLogin(login);
        if (opt.isPresent()) {
            Company company = opt.get();

            // Save historique
            HistoriqueAccount history = new HistoriqueAccount();
            history.setCompanyId(company.getCompanyId());
            history.setDeactivateAt(LocalDateTime.now());
            historiqueRepo.save(history);

            // Deactivate account
            company.setValidateAt(null);
            repo.save(company);
            return true;
        }
        return false;
    }
}
