package com.rafiqstore.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public LocalDateTime convert(String source) {
        try {
            return LocalDateTime.parse(source, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            // If parsing as date-time fails, try parsing as date
            LocalDate date = LocalDate.parse(source, DATE_FORMATTER);
            return date.atStartOfDay();
        }
    }
}
