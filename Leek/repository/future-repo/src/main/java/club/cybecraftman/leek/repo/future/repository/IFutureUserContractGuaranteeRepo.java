package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.repo.future.model.FutureUserContractGuarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureUserContractGuaranteeRepo extends JpaRepository<FutureUserContractGuarantee, Long> {
}
