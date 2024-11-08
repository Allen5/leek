package club.cybecraftman.leek.domain.future;

import club.cybecraftman.leek.repo.future.repository.IFutureContractRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContractService {

    @Autowired
    private IFutureContractRepo contractRepo;
}
