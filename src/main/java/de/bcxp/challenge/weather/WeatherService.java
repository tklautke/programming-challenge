package de.bcxp.challenge.weather;

import de.bcxp.challenge.util.CsvTableReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for loading and analyzing daily weather data
 * from a CSV resource. It provides functionality to determine the day
 * with the smallest temperature spread.
 *
 * <p>The expected CSV format must contain at least the following columns:</p>
 * <ul>
 *     <li><b>Day</b> – day index as an integer</li>
 *     <li><b>MxT</b> – maximum daily temperature</li>
 *     <li><b>MnT</b> – minimum daily temperature</li>
 * </ul>
 *
 * <p>Values may include non-numeric characters (such as asterisks or symbols).
 * These will be stripped automatically before numeric conversion.</p>
 *
 * <p>Any row missing required fields or containing invalid numeric values is
 * skipped gracefully. A message is written to stderr (in production this should
 * be replaced by proper logging).</p>
 */
public class WeatherService {

    private final CsvTableReader csvReader;
    private final String resourcePath;

    /**
     * Creates a new {@link WeatherService}.
     *
     * @param csvReader    the CSV reader to use for parsing input data
     * @param resourcePath classpath-relative path to the weather CSV file
     */
    public WeatherService(CsvTableReader csvReader, String resourcePath) {
        this.csvReader = csvReader;
        this.resourcePath = resourcePath;
    }

    /**
     * Loads the weather CSV and determines the day with the smallest temperature spread.
     *
     * <p>The temperature spread is defined as:</p>
     * <pre>
     *     MxT - MnT
     * </pre>
     *
     * @return the day (as an integer) with the smallest temperature spread
     * @throws IllegalStateException if no valid data rows are found in the CSV
     */
    public int findDayWithSmallestTemperatureSpread() {
        List<Map<String, String>> rows = loadRows();
        return findDayWithSmallestTemperatureSpread(rows);
    }

    /**
     * Internal helper to compute the day with the smallest spread from already-loaded rows.
     *
     * @param rows the parsed CSV rows
     * @return the day number with the smallest (MxT − MnT) spread
     */
    private int findDayWithSmallestTemperatureSpread(List<Map<String, String>> rows) {
        return rows.stream()
                .map(this::toWeatherDaySafely)
                .flatMap(Optional::stream) // skip invalid rows
                .min(Comparator.comparingInt(Weather::getSpread))
                .orElseThrow(() -> new IllegalStateException("No valid weather rows found"))
                .getDay();
    }

    /**
     * Attempts to convert a CSV row to a {@link Weather} instance.
     * Invalid rows are skipped with a warning.
     *
     * @param row a CSV row as a map of column name → value
     * @return an {@code Optional<WeatherDay>} containing the parsed object,
     *         or empty if parsing failed
     */
    private Optional<Weather> toWeatherDaySafely(Map<String, String> row) {
        try {
            String dayStr = row.get("Day");
            String maxStr = row.get("MxT");
            String minStr = row.get("MnT");

            int day = Integer.parseInt(stripNonNumberCharacters(dayStr));
            int max = Integer.parseInt(stripNonNumberCharacters(maxStr));
            int min = Integer.parseInt(stripNonNumberCharacters(minStr));

            return Optional.of(new Weather(day, max, min));

        } catch (RuntimeException e) {
            // In production: replace System.err with proper logger
            System.err.println("Skipping invalid weather row: " + row + " (" + e.getMessage() + ")");
            return Optional.empty();
        }
    }

    /**
     * Removes all characters except digits and minus signs from a string.
     * Useful for cleaning CSV values that include markers (e.g., "86*" or "59#").
     *
     * @param input the raw CSV cell value
     * @return a cleaned numeric string
     * @throws IllegalArgumentException if input is null or no digits remain after cleaning
     */
    private String stripNonNumberCharacters(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        String digits = input.replaceAll("[^0-9\\-]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("No digits in input: '" + input + "'");
        }
        return digits;
    }

    /**
     * Loads and parses the weather CSV file using the configured {@link CsvTableReader}.
     *
     * @return list of maps representing CSV rows
     * @throws IllegalStateException if the resource is not found or cannot be read
     */
    private List<Map<String, String>> loadRows() {
        InputStream input = getClass().getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalStateException("Could not find resource: " + resourcePath);
        }
        try {
            return csvReader.read(input);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading weather CSV file", e);
        }
    }
}
