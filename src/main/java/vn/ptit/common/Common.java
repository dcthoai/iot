package vn.ptit.common;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Common {

    public static void setSqlParams(Query query, Map<String, Object> params) {
        if (Objects.nonNull(params) && !params.isEmpty()) {
            Set<Map.Entry<String, Object>> set = params.entrySet();

            for (Map.Entry<String, Object> obj : set) {
                if (Objects.isNull(obj.getValue()))
                    query.setParameter(obj.getKey(), "");
                else
                    query.setParameter(obj.getKey(), obj.getValue());
            }
        }
    }

    public static String convertTimestampToString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(formatter);
    }

    public static String convertTimestampToString(Timestamp timestamp, String template) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(template);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(formatter);
    }
}
