package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.FutureExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureExchangeRepo extends JpaRepository<FutureExchange, Long> {
}
