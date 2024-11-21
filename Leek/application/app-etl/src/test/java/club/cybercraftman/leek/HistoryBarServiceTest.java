package club.cybercraftman.leek;

import club.cybercraftman.leek.common.exception.LeekException;
import club.cybercraftman.leek.service.HistoryBarService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EtlApplication.class)
@Slf4j
public class HistoryBarServiceTest {


    private static final String FILEPATH = "/Users/allen/Documents/Motionless/QuantLab/data/future/daily/2024.csv";
    private static final String DIR = "/Users/allen/Documents/Motionless/QuantLab/data/future/daily";

    @Autowired
    private HistoryBarService historyBarService;

    @Test
    public void testBatchImport() throws LeekException {
        historyBarService.importBigQuantHistoryBars(DIR, "csv");
    }

    @Test
    public void testYearImport() {
        historyBarService.importBigQuantHistoryBars(FILEPATH, 2024);
    }


}
