package club.cybercraftman.leek.repo.admin.repository;

import club.cybercraftman.leek.infrastructure.database.datasource.AdminDataSourceConfig;
import club.cybercraftman.leek.repo.admin.model.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ConditionalOnBean(AdminDataSourceConfig.class)
public interface IUserRepo extends JpaRepository<User, Long> {

    /**
     * 根据用户名和状态获取用户信息
     * @param username
     * @param status
     * @return
     */
    @Query("select t from User t where t.username = :username and status = :status")
    Optional<User> findOneByUsernameAndStatus(final @Param("username") String username,
                                              final @Param("status") Integer status);

}
