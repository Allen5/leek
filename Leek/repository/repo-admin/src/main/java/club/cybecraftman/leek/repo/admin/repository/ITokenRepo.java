package club.cybecraftman.leek.repo.admin.repository;

import club.cybecraftman.leek.repo.admin.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ITokenRepo extends JpaRepository<Token, Long> {

    @Transactional
    @Modifying
    @Query("delete from Token t where t.userId = :userId")
    void deleteByUserId(final @Param("userId") Long userId);

}