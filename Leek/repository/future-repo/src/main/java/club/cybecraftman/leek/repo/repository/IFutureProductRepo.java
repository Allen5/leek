package club.cybecraftman.leek.repo.repository;

import club.cybecraftman.leek.repo.model.FutureProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureProductRepo extends JpaRepository<FutureProduct, Long> {
}
