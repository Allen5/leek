package club.cybercraftman.leek.domain.financedata.future;

import club.cybercraftman.leek.repo.financedata.model.future.FutureExchange;
import club.cybercraftman.leek.repo.financedata.repository.future.IFutureExchangeRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExchangeService {

    @Autowired
    private IFutureExchangeRepo exchangeRepo;

    /**
     * 获取所有的期货交易所
     * @return
     */
    public List<FutureExchange> fetchAll() {
        // TODO: 转换为DTO
        return exchangeRepo.findAll();
    }

}
