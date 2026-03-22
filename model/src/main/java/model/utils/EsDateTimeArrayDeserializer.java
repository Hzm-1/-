package model.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;

public class EsDateTimeArrayDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // 校验数组格式
        if (!node.isArray() || node.size() != 7) {
            throw new IOException("ES时间格式错误，应为长度为7的数组[年,月,日,时,分,秒,纳秒]，实际：" + node);
        }

        try {
            int year = node.get(0).asInt();
            int month = node.get(1).asInt();
            int day = node.get(2).asInt();
            int hour = node.get(3).asInt();
            int minute = node.get(4).asInt();
            int second = node.get(5).asInt();
            long nano = node.get(6).asLong();

            // 校验时间字段合法性
            if (month < 1 || month > 12) {
                throw new IOException("月份必须在1-12之间，实际值：" + month);
            }
            if (day < 1 || day > 31) {
                throw new IOException("日期必须在1-31之间，实际值：" + day);
            }
            if (hour < 0 || hour > 23) {
                throw new IOException("小时必须在0-23之间，实际值：" + hour);
            }
            if (minute < 0 || minute > 59) {
                throw new IOException("分钟必须在0-59之间，实际值：" + minute);
            }
            if (second < 0 || second > 59) {
                throw new IOException("秒必须在0-59之间，实际值：" + second);
            }
            if (nano < 0 || nano > 999_999_999) {
                throw new IOException("纳秒必须在0-999999999之间，实际值：" + nano);
            }

            // 构建LocalDateTime
            return LocalDateTime.of(year, month, day, hour, minute, second, (int) nano);
        } catch (Exception e) {
            throw new IOException("解析ES时间数组失败（原始数组：" + node + "）：" + e.getMessage(), e);
        }
    }
}