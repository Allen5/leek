package club.cybecraftman.leek.domain.financedata.future;

import club.cybecraftman.leek.common.constant.finance.BarType;
import club.cybecraftman.leek.common.constant.finance.FinanceType;
import club.cybecraftman.leek.common.constant.finance.Market;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.domain.financedata.IBarService;
import club.cybecraftman.leek.repo.financedata.model.future.FutureBar1Day;
import club.cybecraftman.leek.repo.financedata.repository.future.IFutureBar1DayRepo;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public boolean isSupport(Market market, FinanceType financeType, BarType barType) {
        return Market.CN.equals(market) && FinanceType.FUTURE.equals(financeType) && BarType.DAILY.equals(barType);
    }



}
