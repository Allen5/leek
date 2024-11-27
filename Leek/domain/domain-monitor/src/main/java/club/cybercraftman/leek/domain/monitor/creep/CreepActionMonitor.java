package club.cybercraftman.leek.domain.monitor.creep;

import club.cybercraftman.leek.common.constant.CommonExecuteStatus;
import club.cybercraftman.leek.common.constant.creep.DataType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.event.creep.CreepEvent;
import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import club.cybercraftman.leek.repo.monitor.model.CreepLog;
import club.cybercraftman.leek.repo.monitor.repository.ICreeperLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 记录爬虫的执行记录
 */
@Service
@Slf4j
public class CreepActionMonitor {

    @Autowired
    private ICreeperLog creeperLog;

    public CreepLog getById(final Long id) {
        Optional<CreepLog> op = creeperLog.findById(id);
        if ( op.isEmpty() ) {
            log.error("can not find creep log with id: {}", id);
            throw new LeekRuntimeException("数据库执行异常，creep log 不存在. id: " + id);
        }
        return op.get();
    }

    /**
     * 获取在指定时间范围内未失败的记录
     * @param market
     * @param financeType
     * @param dataType
     * @param start
     * @param end
     * @return
     */
    public List<CreepLog> findFailedLogs(final Market market,
                                         final FinanceType financeType,
                                         final DataType dataType,
                                         final LocalDateTime start,
                                         final LocalDateTime end) throws LeekException {
        if ( null == market || null == financeType || null == dataType ) {
            throw new LeekException("[参数错误]market, financeType, dataType均不可为空");
        }
        return creeperLog.findAllByMarketCodeAndFinanceTypeAndDataTypeAndStatusAndUpdatedAt(market.getCode(),
                financeType.getType(),
                dataType.getType(),
                CommonExecuteStatus.FAIL.getStatus(),
                Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * 添加爬虫记录
     */
    @Transactional
    public Long init(final String name, final CreepEvent event) {
        CreepLog creepLog = new CreepLog();
        creepLog.setCreeper(name);
        creepLog.setMarketCode(event.getMarketCode());
        creepLog.setFinanceType(event.getFinanceType());
        creepLog.setDataType(event.getDataType());
        creepLog.setSourceName(event.getSourceName());
        creepLog.setSource(event.getSource());
        creepLog.setStatus(CommonExecuteStatus.EXECUTING.getStatus());
        creepLog.setCreatedAt(new Date());
        creepLog = creeperLog.save(creepLog);
        return creepLog.getId();
    }

    @Transactional
    public void fail(final Long logId, final String errMessage) {
        CreepLog creepLog = getById(logId);
        creepLog.setErrCause(errMessage);
        creepLog.setStatus(CommonExecuteStatus.FAIL.getStatus());
        creepLog.setUpdatedAt(new Date());
        creeperLog.save(creepLog);
    }

    @Transactional
    public void success(final Long logId) {
        CreepLog creepLog = getById(logId);
        creepLog.setStatus(CommonExecuteStatus.SUCCESS.getStatus());
        creepLog.setUpdatedAt(new Date());
        creeperLog.save(creepLog);
    }



}
