package club.cybecraftman.leek.domain.backtest;

import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 回测执行器
 */
@Service
@Slf4j
public class BackTestRunner {

    /**
     * 根据市场和金融产品初始化回测策略执行计划
     * @param market
     * @param financeType
     */
    @Transactional
    public void generate(final Market market, final FinanceType financeType) {
        // step1: 获取品种列表
        // step2: 获取参数表
    }

    /**
     * 开始执行回测
     */
    public void start() {

    }

}
