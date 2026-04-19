package com.aja.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Herramienta para poner las fechas de forma que se entiendan fácilmente.
 */
public class DateUtils {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES"));

    /**
     * Cambia el orden de la fecha (de Año-Mes-Día a Día/Mes/Año). 
     * Si algo falla, la deja como estaba.
     */
    public static String format(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return "N/A";
        }
        try {
            // Intentamos parsear considerando que puede venir con hora (ISO_DATE_TIME) o solo fecha
            LocalDate date;
            if (dateStr.contains("T")) {
                date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
            } else {
                date = LocalDate.parse(dateStr, INPUT_FORMATTER);
            }
            return date.format(OUTPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }
}