package club.cybecraftman.leek.controller.creeper;

import club.cybecraftman.leek.common.dto.BaseResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/admin/creeper-config")
@Slf4j
public class CreeperConfigController {

    /**
     * 获取巡航配置列表
     * @return
     */
    @PostMapping(path = "/list")
    public BaseResultDTO<List> list() {
        return BaseResultDTO.success(null);
    }

    /**
     * 新增巡航配置
     * @return
     */
    @PostMapping(path = "/add")
    public BaseResultDTO<Boolean> add() {
        return BaseResultDTO.success(true);
    }

    /**
     * 修改巡航配置
     * @return
     */
    @PostMapping(path = "/modify")
    public BaseResultDTO<Boolean> modify() {
        return BaseResultDTO.success(true);
    }

    /**
     * 删除巡航配置
     * @return
     */
    @PostMapping(path = "/remove")
    public BaseResultDTO<Boolean> remove() {
        return BaseResultDTO.success(true);
    }

}
