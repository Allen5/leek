package club.cybecraftman.leek.domain.future;

import club.cybecraftman.leek.repo.future.repository.IFutureBar1DayRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BarService {

    @Autowired
    private IFutureBar1DayRepo bar1DayRepo;

}
