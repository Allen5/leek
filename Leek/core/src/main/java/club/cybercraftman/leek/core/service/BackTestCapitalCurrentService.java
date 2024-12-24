package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.constant.trade.CapitalCurrentCategory;
import club.cybercraftman.leek.common.constant.trade.CapitalCurrentType;
import club.cybercraftman.leek.repo.trade.model.backtest.BackTestCapitalCurrent;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestCapitalCurrentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class BackTestCapitalCurrentService {

    @Autowired
    private IBackTestCapitalCurrentRepo capitalCurrentRepo;

    /**
     * 扣减手续费、服务费
     * @param recordId
     * @param date
     * @param amount
     */
    @Transactional
    public void subCommission(final Long recordId,
                              final Date date,
                              final BigDecimal amount) {
        BackTestCapitalCurrent current = new BackTestCapitalCurrent();
        current.setRecordId(recordId);
        current.setType(CapitalCurrentType.OUTCOME.getType());
        current.setCategory(CapitalCurrentCategory.COMMISSION.getCategory());
        current.setDatetime(date);
        current.setAmount(amount);
        capitalCurrentRepo.save(current);
    }

    /**
     * 扣减保证金
     * @param recordId
     * @param date
     * @param amount
     */
    @Transactional
    public void subDeposit(final Long recordId,
                           final Date date,
                           final BigDecimal amount) {
        BackTestCapitalCurrent current = new BackTestCapitalCurrent();
        current.setRecordId(recordId);
        current.setType(CapitalCurrentType.OUTCOME.getType());
        current.setCategory(CapitalCurrentCategory.DEPOSIT.getCategory());
        current.setDatetime(date);
        current.setAmount(amount);
        capitalCurrentRepo.save(current);
    }

    /**
     * 退回保证金
     * @param recordId
     * @param date
     * @param amount
     */
    @Transactional
    public void fallbackDeposit(final Long recordId,
                                final Date date,
                                final BigDecimal amount) {
        BackTestCapitalCurrent current = new BackTestCapitalCurrent();
        current.setRecordId(recordId);
        current.setType(CapitalCurrentType.INCOME.getType());
        current.setCategory(CapitalCurrentCategory.DEPOSIT.getCategory());
        current.setDatetime(date);
        current.setAmount(amount);
        capitalCurrentRepo.save(current);
    }

    /**
     * 记录收益
     * @param recordId
     * @param date
     * @param amount
     */
    @Transactional
    public void addProfit(final Long recordId,
                          final Date date,
                          final BigDecimal amount) {
        BackTestCapitalCurrent current = new BackTestCapitalCurrent();
        current.setRecordId(recordId);
        current.setType(CapitalCurrentType.INCOME.getType());
        current.setCategory(CapitalCurrentCategory.PROFIT.getCategory());
        current.setDatetime(date);
        current.setAmount(amount);
        capitalCurrentRepo.save(current);
    }

    /**
     * 记录净收益
     * @param recordId
     * @param date
     * @param amount
     */
    @Transactional
    public void addNet(final Long recordId,
                       final Date date,
                       final BigDecimal amount) {
        BackTestCapitalCurrent current = new BackTestCapitalCurrent();
        current.setRecordId(recordId);
        current.setType(CapitalCurrentType.INCOME.getType());
        current.setCategory(CapitalCurrentCategory.NET.getCategory());
        current.setDatetime(date);
        current.setAmount(amount);
        capitalCurrentRepo.save(current);
    }

}
