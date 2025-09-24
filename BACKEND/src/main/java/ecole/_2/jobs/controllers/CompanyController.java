package ecole._2.jobs.controllers;

import ecole._2.jobs.dto.ApiResponse;
import ecole._2.jobs.dto.LoginRequest;
import ecole._2.jobs.entities.Company;
import ecole._2.jobs.services.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs/companies")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    // Create a company
    @PostMapping
    public ApiResponse<Company> createCompany(@RequestBody Company company) {
        try {
            Company saved = service.saveCompany(company);
            return new ApiResponse<>(true, saved, "Company created successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Check if validated by login
    @GetMapping("/is-validated")
    public ApiResponse<Company> isCompanyValidatedByLogin(@RequestParam String login) {
        try {
            Optional<Company> opt = service.getCompanyByLogin(login);
            if (opt.isPresent()) {
                boolean validated = opt.get().getValidateAt() != null;
                if (validated) {
                    return new ApiResponse<>(true, opt.get(), "Company is validated");
                }
                return new ApiResponse<>(false, opt.get(), "Company not validated");
            } else {
                return new ApiResponse<>(false, null, "Company not found");
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Activate company (set validateAt now)
    @PostMapping("/activate")
    public ApiResponse<Company> activateCompany(@RequestParam String login) {
        try {
            Optional<Company> opt = service.getCompanyByLogin(login);
            if (opt.isPresent()) {
                boolean result = service.activateCompany(login);
                if (result) return new ApiResponse<>(true, opt.get(), "Company activated");
                else return new ApiResponse<>(false, opt.get(), "Company not found");
            } else {
                return new ApiResponse<>(false, null, "Company not found");
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Deactivate company (set validateAt null + save historique)
    @PostMapping("/deactivate")
    public ApiResponse<Company> deactivateCompany(@RequestParam String login) {
        try {
            Optional<Company> opt = service.getCompanyByLogin(login);
            if (opt.isPresent()) {
                boolean result = service.deactivateCompany(login);
                if (result) return new ApiResponse<>(true, opt.get(), "Company deactivated");
                else return new ApiResponse<>(false, opt.get(), "Company not found");
            } else {
                return new ApiResponse<>(false, null, "Company not found");
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ApiResponse<Company> authenticateCompany(@RequestBody LoginRequest request) {
        try {
            Optional<Company> opt = service.getCompanyByLogin(request.getLogin());
            if (opt.isEmpty()) {
                return new ApiResponse<>(false, null, "Login not found");
            }

            Company company = opt.get();
            String hashedInput = hashPasswordSHA256(request.getPassword());

            if (!hashedInput.equals(company.getPassword())) {
                return new ApiResponse<>(false, null, "Invalid password");
            }

            if (company.getValidateAt() == null) {
                return new ApiResponse<>(false, null, "Account not validated");
            }

            return new ApiResponse<>(true, company, "Authentication successful");

        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    private String hashPasswordSHA256(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    // Get all companies
    @GetMapping
    public ApiResponse<List<Company>> getAllCompanies() {
        try {
            List<Company> companies = service.getAllCompanies();
            return new ApiResponse<>(true, companies, "Success");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Get company by ID
    @GetMapping("/{id}")
    public ApiResponse<Company> getCompanyById(@PathVariable Long id) {
        try {
            Optional<Company> company = service.getCompanyById(id);
            if (company.isPresent())
                return new ApiResponse<>(true, company.get(), "Success");
            else
                return new ApiResponse<>(false, null, "Company not found");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Update company
    @PutMapping("/{id}")
    public ApiResponse<Company> updateCompany(@PathVariable Long id, @RequestBody Company company) {
        try {
            company.setCompanyId(id);
            Company saved = service.saveCompany(company);
            return new ApiResponse<>(true, saved, "Company updated successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Delete company
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteCompany(@PathVariable Long id) {
        try {
            service.deleteCompany(id);
            return new ApiResponse<>(true, true, "Company deleted successfully");
        } catch (Exception e) {
            return new ApiResponse<>(false, false, e.getMessage());
        }
    }
}
