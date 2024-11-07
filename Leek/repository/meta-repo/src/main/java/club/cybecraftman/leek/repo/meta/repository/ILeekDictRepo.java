package club.cybecraftman.leek.repo.meta.repository;

import club.cybecraftman.leek.repo.meta.model.LeekDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILeekDictRepo extends JpaRepository<LeekDict, String> {
}
