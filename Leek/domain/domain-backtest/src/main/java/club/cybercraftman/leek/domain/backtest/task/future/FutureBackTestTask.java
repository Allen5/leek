package club.cybercraftman.leek.domain.backtest.task.future;

import club.cybercraftman.leek.common.bean.DateRange;
import club.cybercraftman.leek.common.context.SpringContextUtil;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.domain.backtest.task.BackTestTask;
import club.cybercraftman.leek.repo.financedata.repository.IFutureBackTestRepo;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Tuple;
import java.util.Date;

@Builder
@Slf4j
public class FutureBackTestTask extends BackTestTask {


    @Override
    protected DateRange calcDateRange(String code, Integer startPercent, Integer endPercent) {
        // 根据品种获取数据的时间范围
        IFutureBackTestRepo repo = SpringContextUtil.getBean(IFutureBackTestRepo.class);
        Long count = repo.countByCode(code);
        if ( null == count ) {
            throw new LeekRuntimeException("品种: " + code + "不存在交易行情数据");
        }
        int offset = (int) (startPercent * count); // 需要跳过的条数
        int size = (int) (count * (endPercent - startPercent) / 100.0); // 取值范围
        Tuple tuple = repo.findDateRangeByCode(code, offset, size);
        return new DateRange(tuple.get("start", Date.class), tuple.get("end", Date.class));
    }

    @Override
    protected Integer countBars(String code, Date start, Date end) {
        IFutureBackTestRepo repo = SpringContextUtil.getBean(IFutureBackTestRepo.class);
        return repo.countByCodeAndDateTimeRange(code, start, end);
    }



}
