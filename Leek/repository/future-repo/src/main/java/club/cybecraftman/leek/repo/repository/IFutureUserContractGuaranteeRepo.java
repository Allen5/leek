package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.FutureUserContractGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureUserContractGuaranteeRepo extends JpaRepository<FutureUserContractGuarantee, Long> {
}
