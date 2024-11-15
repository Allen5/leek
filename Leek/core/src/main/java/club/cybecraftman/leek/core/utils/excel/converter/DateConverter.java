package club.cybecraftman.leek.core.utils.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<Date> {

    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String content = cellData.getStringValue();
        content = content.replaceAll(",", "");
        if ( !StringUtils.hasText(content) ) {
            return null;
        }
        SimpleDateFormat sdf;
        if ( content.contains("/") ) {
            sdf = new SimpleDateFormat("yyyy/MM/dd");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        return sdf.parse(content);
    }
}
