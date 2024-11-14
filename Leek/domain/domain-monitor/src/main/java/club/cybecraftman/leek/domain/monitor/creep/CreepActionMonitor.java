package club.cybecraftman.leek.domain.monitor.creep;

import club.cybecraftman.leek.common.constant.CommonExecuteStatus;
import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import club.cybecraftman.leek.repo.monitor.model.CreepLog;
import club.cybecraftman.leek.repo.monitor.repository.ICreeperLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

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
