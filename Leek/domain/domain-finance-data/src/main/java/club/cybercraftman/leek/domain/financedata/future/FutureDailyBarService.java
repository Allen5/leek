package club.cybercraftman.leek.domain.financedata.future;

import club.cybercraftman.leek.common.constant.finance.BarType;
import club.cybercraftman.leek.common.constant.finance.FinanceType;
import club.cybercraftman.leek.common.constant.finance.Market;
import club.cybercraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybercraftman.leek.domain.financedata.IBarService;
import club.cybercraftman.leek.repo.financedata.model.future.FutureBar1Day;
import club.cybercraftman.leek.repo.financedata.repository.future.IFutureBar1DayRepo;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FutureDailyBarService implements IBarService {

    @Autowired
    private IFutureBar1DayRepo bar1DayRepo;

    @Override
    @Transactional
    public void handleBars(JSONArray data) {
        List<FutureBarEventData> bars = data.toJavaList(FutureBarEventData.class);
        // 先删除
        bars.forEach(b -> bar1DayRepo.deleteByDateTimeAndSymbol(b.getDatetime(), b.getSymbol()));
        // 再新增
        List<FutureBar1Day> items = bars.stream().map(b -> {
            FutureBar1Day item = new FutureBar1Day();
            item.setDatetime(b.getDatetime());
            item.setProductCode(b.getProductCode());
            item.setContractCode(b.getContractCode());
            item.setSymbol(b.getSymbol());
            item.setOpen(b.getOpen());
            item.setHigh(b.getHigh());
            item.setLow(b.getLow());
            item.setClose(b.getClose());
            item.setSettle(b.getSettle());
            item.setOpenInterest(b.getOpenInterest());
            item.setVolume(b.getVolume());
            item.setAmount(b.getAmount());
            return item;
        }).collect(Collectors.toList());
        bar1DayRepo.saveAll(items);
    }

    @Override
    public void truncateBars() {
        // 批量删除
        bar1DayRepo.deleteAllInBatch();
    }

    @Override
    @Transactional
    public void truncateBarsByYear(Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 0); // 1月
        calendar.set(Calendar.DAY_OF_YEAR, 1); // 1日
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();

        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date end = calendar.getTime();

        bar1DayRepo.deleteAllByDateTime(start, end);
    }

    @Override
    public void batchInsert(List<FutureBarEventData> bars) {
        List<FutureBar1Day> items = bars.stream().map(b -> {
            FutureBar1Day item = new FutureBar1Day();
            item.setDatetime(b.getDatetime());
            item.setProductCode(b.getProductCode());
            item.setContractCode(b.getContractCode());
            item.setSymbol(b.getSymbol());
            item.setOpen(b.getOpen());
            item.setHigh(b.getHigh());
            item.setLow(b.getLow());
            item.setClose(b.getClose());
            item.setSettle(b.getSettle());
            item.setOpenInterest(b.getOpenInterest());
            item.setVolume(b.getVolume());
            item.setAmount(b.getAmount());
            return item;
        }).collect(Collectors.toList());
        bar1DayRepo.saveAll(items);
    }


    @Override
    public boolean isSupport(Market market, FinanceType financeType, BarType barType) {
        return Market.CN.equals(market) && FinanceType.FUTURE.equals(financeType) && BarType.DAILY.equals(barType);
    }



}
