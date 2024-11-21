package club.cybercraftman.leek.domain.backtest;

import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 回测执行器
 */
@Service
@Slf4j
public class BackTestExecutor {

    /**
     * 根据市场和金融产品初始化回测策略执行计划
     * @param market
     * @param financeType
     */
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
