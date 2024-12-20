package club.cybercraftman.leek.controller.account;

import club.cybercraftman.leek.common.dto.BaseResultDTO;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.domain.admin.AccountService;
import club.cybercraftman.leek.dto.account.LoginReqDTO;
import club.cybercraftman.leek.dto.account.LoginRespDTO;
import club.cybercraftman.leek.dto.account.TokenRenewReqDTO;
import club.cybercraftman.leek.repo.admin.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(path = "/api/admin/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 登录
     * @return
     */
    @PostMapping(path = "/login")
    public BaseResultDTO<LoginRespDTO> login(final @RequestBody LoginReqDTO dto) throws LeekException {
        log.debug("login request dto: {}", dto);
        Token token = accountService.login(dto.getUsername(), dto.getPassword());
        return BaseResultDTO.success(LoginRespDTO.builder()
                .token(token.getToken())
                .refreshToken(token.getRefreshToken())
                .build());
    }

    /**
     * token续订
     * @return
     */
    @PostMapping(path = "/token/renew")
    public BaseResultDTO<Object> renewToken(final @RequestBody TokenRenewReqDTO dto) {
        log.debug("token renew request dto: {}", dto);
        return null;
    }

}
