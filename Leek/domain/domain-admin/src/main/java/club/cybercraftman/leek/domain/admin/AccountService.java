package club.cybercraftman.leek.domain.admin;

import club.cybercraftman.leek.common.constant.admin.UserStatus;
import club.cybercraftman.leek.common.crypt.MD5Util;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.repo.admin.model.Token;
import club.cybercraftman.leek.repo.admin.model.User;
import club.cybercraftman.leek.repo.admin.repository.ITokenRepo;
import club.cybercraftman.leek.repo.admin.repository.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private ITokenRepo tokenRepo;

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Transactional
    public Token login(final String username, final String password) throws LeekException {
        Optional<User> op = userRepo.findOneByUsernameAndStatus(username, UserStatus.NORMAL.getStatus());
        if ( op.isEmpty() ) {
            throw new LeekException("用户不存在: " + username);
        }
        User user = op.get();
        String candidate = MD5Util.generate(password).toUpperCase();
        if ( !user.getPassword().equals(candidate) ) {
            throw new LeekException("用户名或密码错误");
        }
        // 先删除
        tokenRepo.deleteByUserId(user.getId());
        // 再新建
        Token token = new Token();
        token.setUserId(user.getId());
        token.setToken(tokenService.generate(user));
        token.setRefreshToken(tokenService.generateRefreshToken(user));
        token.setCreatedAt(new Date());
        token.setUpdatedAt(new Date());
        tokenRepo.save(token);
        return token;
    }

}
