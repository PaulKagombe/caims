package com.countyassembly.caims.report;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Small CSV writer — handles the common escaping cases (commas,
 * quotes, newlines) without pulling in a full CSV library for
 * what is otherwise a very simple export need.
 */
public final class CsvExporter {

    private CsvExporter() {
    }

    public static void write(
            HttpServletResponse response,
            String filename,
            List<String> headers,
            List<List<String>> rows) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        PrintWriter writer = response.getWriter();

        writer.println(toCsvLine(headers));

        for (List<String> row : rows) {
            writer.println(toCsvLine(row));
        }

        writer.flush();
    }

    private static String toCsvLine(List<String> values) {

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {

            if (i > 0) {
                line.append(",");
            }

            line.append(escape(values.get(i)));
        }

        return line.toString();
    }

    private static String escape(String value) {

        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
