package club.cybercraftman.leek.dto.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeConverter implements Converter<Date> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String content = cellData.getStringValue();
        if ( !StringUtils.hasText(content) ) {
            return null;
        }
        SimpleDateFormat sdf = null;
        if ( content.contains("/") ) {
            sdf = new SimpleDateFormat("yyyy/MM/dd");
        } else if ( content.contains("-") ) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            return null;
        }
        return sdf.parse(content);
    }
}
