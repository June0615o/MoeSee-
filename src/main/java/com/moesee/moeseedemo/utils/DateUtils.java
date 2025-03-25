package com.moesee.moeseedemo.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtils {
    public LocalDate stringToLocalDate(String dateStr) {
        // TODO: "2023-05-31"
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, df);
    }
}
