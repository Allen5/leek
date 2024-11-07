package club.cybecraftman.leek.creeper;

import club.cybecraftman.leek.common.dto.event.creep.CreepEvent;
import club.cybecraftman.leek.common.exception.LeekRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CreeperBuilder {

    @Autowired
    private List<BaseCreeper> creepers;

    /**
     * 创建爬虫实例
     * @return
     */
    public BaseCreeper build(final CreepEvent event) {
        if ( null == creepers ) {
            throw new LeekRuntimeException("can not find creeper for market: " + event.getMarketCode()
                    + ", financeType: " + event.getFinanceType()
                    + ", dataType: " + event.getDataType()
                    + ", sourceName: " + event.getSourceName());
        }
        Optional<BaseCreeper> op = creepers.stream().filter(c -> c.isSupport(event)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("can not find creeper for market: " + event.getMarketCode()
                    + ", financeType: " + event.getFinanceType()
                    + ", dataType: " + event.getDataType()
                    + ", sourceName: " + event.getSourceName());
        }
        BaseCreeper creeper = op.get();
        creeper.setEvent(event);
        return creeper;
    }

}
