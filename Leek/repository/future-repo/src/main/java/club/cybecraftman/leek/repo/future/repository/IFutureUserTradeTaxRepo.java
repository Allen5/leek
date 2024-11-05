package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.repo.future.model.FutureUserTradeTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureUserTradeTaxRepo extends JpaRepository<FutureUserTradeTax, Long> {
}
