package de.bcxp.challenge.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simple CSV table reader that:
 *  - uses the first row as header
 *  - returns each row as Map<headerName, cellValue>
 */
public class CsvTableReader {

    private final CSVFormat format;

    public CsvTableReader(char separator) {
        this.format = CSVFormat.DEFAULT
                .builder()
                .setDelimiter(separator)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .setHeader()            // first row as header
                .setSkipHeaderRecord(true)
                .build();
    }

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
