package com.springboot.aldiabackjava.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
@Component
public class Conversores {
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("d/MM/yyyy", new Locale("es", "ES"));

    public Boolean convertirABoolean(String valor) {
        if (valor == null || valor.trim().isBlank()) return null;
        valor = valor.trim().toUpperCase();
        if (valor.equals("VERDADERO") || valor.equals("TRUE") || valor.equals("1") || valor.equals("SI")) return true;
        if (valor.equals("FALSO") || valor.equals("FALSE") || valor.equals("0") || valor.equals("NO")) return false;
        return null;
    }

    public Long parseLongSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Long.parseLong(value.trim().replace("\"", ""));
        } catch (NumberFormatException e) {
            log.warn("Valor no numérico para Long: {}", value);
            return null;
        }
    }

    public Integer parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim().replace("\"", ""));
        } catch (NumberFormatException e) {
            log.warn("Valor no numérico para Integer: {}", value);
            return null;
        }
    }

    public Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim().replace("\"", ""));
        } catch (NumberFormatException e) {
            log.warn("Valor no numérico para Double: {}", value);
            return null;
        }
    }

    public String cleanField(String value) {
        if (value == null) return null;
        return value.trim().replace("\"", "");
    }

    public String safe(Object value) {
        if (value == null) return "";
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    public Date parseFecha(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        try {
            return formatoFecha.parse(valor.trim().toLowerCase());
        } catch (Exception e) {
            return null;
        }
    }

    public Long getLongValue(Object obj) {
        return (obj instanceof Number) ? ((Number) obj).longValue() : null;
    }

    public boolean convertirABooleanFlexible(Object value) {
        if (value == null) return false;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }
}
