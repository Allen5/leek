package club.cybecraftman.leek.domain.future;

import club.cybecraftman.leek.repo.future.repository.IFutureProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private IFutureProductRepo productRepo;


}
