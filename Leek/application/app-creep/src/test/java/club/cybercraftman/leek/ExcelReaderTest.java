package club.cybercraftman.leek;

import club.cybecraftman.leek.CreepApplication;
import club.cybecraftman.leek.common.event.etl.future.FutureBarEventData;
import club.cybecraftman.leek.reader.future.CZCEExcelReader;
import club.cybecraftman.leek.reader.future.DCEExcelReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = CreepApplication.class)
@Slf4j
public class ExcelReaderTest {

    @Test
    public void testCZCEReader() throws ParseException {
        final String filepath = "download/CZCE_2024-11-14.xls";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<FutureBarEventData> items = CZCEExcelReader.readDailyBar(simpleDateFormat.parse("2024-11-14"), filepath);
        log.info("load items: {}", items.size());
    }

    @Test
    public void testDCEReader() {
        final String filepath = "download/DCE_20241114.xls";
        List<FutureBarEventData> items = DCEExcelReader.readDailyBar(new Date(), filepath);
        log.info("load items: {}", items.size());
    }

}
