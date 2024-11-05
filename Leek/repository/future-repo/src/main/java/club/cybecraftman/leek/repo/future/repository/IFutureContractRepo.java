package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.repo.future.model.FutureContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureContractRepo extends JpaRepository<FutureContract, Long> {
}
