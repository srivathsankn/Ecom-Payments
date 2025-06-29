package com.srivath.payment.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatter {

    public static String convertEpochTimeToDate(long epochTimeMillis) {
        // 1. Convert epoch milliseconds to an Instant
        Instant instant = Instant.ofEpochMilli(epochTimeMillis);

        // 2. Convert Instant to LocalDate in a specific time zone
        // Using system default time zone for demonstration.
        // You can specify a different ZoneId if needed (e.g., ZoneId.of("America/New_York"))
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();

        // 3. Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 4. Format the LocalDate to a String
        String formattedDate = localDate.format(formatter);

        return formattedDate;
    }

    public static String convertDateToYYYYMMDDFormat (Date date)
    {

        // 1. Create an instance of SimpleDateFormat with the desired pattern
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 2. Format the Date object into a String
        String formattedDate = sdf.format(date);
        return formattedDate;

    }
}
