package club.cybecraftman.leek.controller;

import club.cybecraftman.leek.common.dto.BaseResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(path = "/api/admin/account")
public class AccountController {

    /**
     * 登录
     * @return
     */
    @PostMapping(path = "/login")
    public BaseResultDTO<Object> login() {
        return null;
    }

    /**
     * token续订
     * @return
     */
    @PostMapping(path = "/token/renew")
    public BaseResultDTO<Object> renewToken() {
        return null;
    }

}
