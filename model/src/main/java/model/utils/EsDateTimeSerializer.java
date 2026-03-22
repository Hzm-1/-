package model.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.time.LocalDateTime;

public class EsDateTimeSerializer extends StdScalarSerializer<LocalDateTime> {

    public EsDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 提取时间各部分，组成7元素数组
        int[] esTimeArray = new int[]{
            value.getYear(),
            value.getMonthValue(), // 月份：1-12（ES直接使用）
            value.getDayOfMonth(),
            value.getHour(),
            value.getMinute(),
            value.getSecond(),
            value.getNano()       // 纳秒：0-999_999_999
        };
        gen.writeArray(esTimeArray, 0, esTimeArray.length);
    }
}