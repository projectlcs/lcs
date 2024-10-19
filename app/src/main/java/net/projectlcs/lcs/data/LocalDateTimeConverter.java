package net.projectlcs.lcs.data;

import androidx.room.TypeConverter;
import java.time.LocalDateTime;


// code from https://stackoverflow.com/questions/54927913/room-localdatetime-typeconverter
public class LocalDateTimeConverter {

    @TypeConverter
    public static LocalDateTime toDate(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            return LocalDateTime.parse(dateString);
        }
    }

    @TypeConverter
    public static String toDateString(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }
}