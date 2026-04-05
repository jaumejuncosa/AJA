package com.aja.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilidad para formatear fechas de la API a un formato amigable para el usuario.
 */
public class DateUtils {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.of("es", "ES"));

    /**
     * Convierte una fecha ISO (yyyy-MM-dd) a formato corto (dd/MM/yyyy).
     * Si hay un error en el parseo, devuelve el string original.
     */
    public static String format(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return "N/A";
        }
        try {
            // La API suele devolver YYYY-MM-DD
            LocalDate date = LocalDate.parse(dateStr, INPUT_FORMATTER);
            return date.format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            return dateStr;
        }
    }
}