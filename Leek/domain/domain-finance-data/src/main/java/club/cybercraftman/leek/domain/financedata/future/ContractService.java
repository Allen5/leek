package club.cybercraftman.leek.domain.financedata.future;

import club.cybercraftman.leek.repo.financedata.repository.future.IFutureContractRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContractService {

    @Autowired
    private IFutureContractRepo contractRepo;
}
