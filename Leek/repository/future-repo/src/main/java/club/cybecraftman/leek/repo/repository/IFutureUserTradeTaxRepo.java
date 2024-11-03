package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.FutureUserTradeTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureUserTradeTaxRepo extends JpaRepository<FutureUserTradeTax, Long> {
}
