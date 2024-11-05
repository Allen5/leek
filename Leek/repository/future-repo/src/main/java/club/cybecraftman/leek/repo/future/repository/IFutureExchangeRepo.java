package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.repo.future.model.FutureExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureExchangeRepo extends JpaRepository<FutureExchange, Long> {
}
