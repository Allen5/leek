package club.cybecraftman.leek.repo.future.repository;

import club.cybecraftman.leek.repo.future.model.FutureProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFutureProductRepo extends JpaRepository<FutureProduct, Long> {
}
