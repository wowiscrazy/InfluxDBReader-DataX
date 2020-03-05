package com.zyt.datax.plugin.reader.tsdbreader.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @ClassName TimeUtils
 * @Description: TimeUtils
 * @Author ZYT
 * @Date 2020/3/4
 * @Version V1.0
 **/
public class TimeUtils {
    public static String getLastMinute(){
        long lastMinuteMilli=LocalDateTime.now().plusMinutes(-1).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return String.valueOf(lastMinuteMilli);
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }
}
