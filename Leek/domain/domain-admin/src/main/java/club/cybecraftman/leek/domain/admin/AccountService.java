package club.cybecraftman.leek.domain.admin;

import club.cybecraftman.leek.common.constant.admin.UserStatus;
import club.cybecraftman.leek.common.exception.LeekException;
import club.cybecraftman.leek.domain.admin.vo.Token;
import club.cybecraftman.leek.repo.admin.model.User;
import club.cybecraftman.leek.repo.admin.repository.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserRepo userRepo;

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    public Token login(final String username, final String password) throws LeekException {
        Optional<User> op = userRepo.findOneByUsernameAndStatus(username, UserStatus.NORMAL.getStatus());
        if ( op.isEmpty() ) {
            throw new LeekException("用户不存在: " + username);
        }
        User user = op.get();
        // TODO: 密码加密
        if ( !user.getPassword().equalsIgnoreCase(password) ) {
            throw new LeekException("用户名或密码错误");
        }
        // TODO: 存入数据库
        return Token.builder()
                .token(tokenService.generate(user))
                .refreshToken(tokenService.generateRefreshToken(user))
                .build();
    }

}
