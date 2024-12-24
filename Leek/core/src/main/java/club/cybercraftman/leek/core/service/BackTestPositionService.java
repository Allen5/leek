package club.cybercraftman.leek.core.service;

import club.cybercraftman.leek.common.constant.trade.PositionStatus;
import club.cybercraftman.leek.repo.trade.repository.backtest.IBackTestPositionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BackTestPositionService {

    @Autowired
    private IBackTestPositionRepo backTestPositionRepo;

    /**
     * 检查是否有持仓
     * @param recordId
     * @param symbol
     * @return
     */
    public boolean hasEnoughPosition(Long recordId, String symbol, Integer volume) {
        Long availableVolume = backTestPositionRepo.sumVolumeByRecordIdAndSymbolAndStatus(recordId, symbol, PositionStatus.OPEN.getStatus());
        return availableVolume != null && availableVolume >= volume;
    }

}
