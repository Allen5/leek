package club.cybercraftman.leek.domain.financedata.future;

import club.cybercraftman.leek.repo.financedata.repository.future.IFutureProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private IFutureProductRepo productRepo;


}
