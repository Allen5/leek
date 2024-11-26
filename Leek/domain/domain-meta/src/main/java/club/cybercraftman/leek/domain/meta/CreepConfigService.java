package club.cybercraftman.leek.domain.meta;

import club.cybercraftman.leek.common.constant.ValidStatus;
import club.cybercraftman.leek.common.constant.creep.DataType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.repo.meta.model.CreeperConfig;
import club.cybercraftman.leek.repo.meta.repository.ICreeperConfigRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class CreepConfigService {

    @Autowired
    private ICreeperConfigRepo configRepo;

    /**
     * 获取当前时间为工作时间，状态为有效的creepers
     * @return
     */
    public List<CreeperConfig> findAvailableCreeps(final Market market,
                                                   final FinanceType financeType,
                                                   final DataType dataType) throws LeekException {
        if ( null == market || null == financeType || null == dataType ) {
            throw new LeekException("[参数错误]makert, financeType, dataType均不可为空");
        }
        LocalTime now = LocalTime.now().withNano(0);
        return configRepo.findAllByMarketCodeAnFinanceTypeAndDataTypeAndStatusAndTime(market.getCode(),
                financeType.getType(),
                dataType.getType(),
                ValidStatus.VALID.getStatus(),
                now);
    }

}
