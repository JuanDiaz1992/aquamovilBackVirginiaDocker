package com.springboot.aldiabackjava.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Slf4j
public class GetDateNow {
    public  static String getCode(){
        Instant instant = Instant.now();
        ZoneId zonaHoraria = ZoneId.systemDefault();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return formato.format(instant.atZone(zonaHoraria));
    }

    public static String getOnlyYearAndMonth(Date date){
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
        return formatYear.format(date)+"-"+formatMonth.format(date);
    }

    public static java.sql.Date formatDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format.format(date);
        return java.sql.Date.valueOf(newDate);
    }
}
