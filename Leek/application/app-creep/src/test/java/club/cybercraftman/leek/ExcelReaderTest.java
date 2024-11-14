package club.cybercraftman.leek;

import club.cybecraftman.leek.CreepApplication;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.reader.future.CZCEExcelReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = CreepApplication.class)
@Slf4j
public class ExcelReaderTest {

    @Test
    public void testCZCEReader() {
        final String filepath = "download/CZCE_2024-11-14.xls";
        List<FutureBarEventData> items = CZCEExcelReader.readDailyBar(new Date(), filepath);
        log.info("load items: {}", items.size());
    }

}
