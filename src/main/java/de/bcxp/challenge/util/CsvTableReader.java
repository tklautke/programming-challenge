package de.bcxp.challenge.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple helper class for reading CSV files.
 * Assumptions:
 *  - The first row contains column headers.
 *  - All following rows are data rows.
 *  - The separator (e.g., ',' or ';') is set in the constructor.
 * Provides a method to read from an InputStream and a method to load
 * a CSV file directly from the classpath using a resource path.
 */
public class CsvTableReader {

    private final CSVFormat format;

    public CsvTableReader(char separator) {
        this.format = CSVFormat.DEFAULT
                .builder()
                .setDelimiter(separator)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
    }

    /**
     * Reads a CSV file from a classpath resource path.
     *
     * @param resourcePath path to the CSV file (e.g. "/data/weather.csv")
     * @return list of CSV rows (as Map<String, String>)
     */
    public List<Map<String, String>> readFromResource(String resourcePath) {

        InputStream input = CsvTableReader.class.getResourceAsStream(resourcePath);

        if (input == null) {
            throw new IllegalStateException("Could not find resource: " + resourcePath);
        }

        try {
            return read(input);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading CSV file: " + resourcePath, e);
        }
    }

    /**
     * Reads a CSV file from an InputStream.
     */
    public List<Map<String, String>> read(InputStream inputStream) throws IOException {

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            List<Map<String, String>> rows = new ArrayList<>();
            List<String> headers = parser.getHeaderNames();

            for (CSVRecord record : parser) {
                Map<String, String> row = new LinkedHashMap<>();

                for (String header : headers) {
                    row.put(header, record.get(header));
                }

                rows.add(row);
            }

            return rows;
        }
    }
}