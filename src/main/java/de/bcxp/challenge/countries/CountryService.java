package de.bcxp.challenge.countries;

import de.bcxp.challenge.util.CsvTableReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for loading and analyzing country information
 * from a CSV resource. The service determines the country with the
 * highest population density, based on values parsed from the CSV.
 *
 * <p>The CSV must contain at minimum the following columns:</p>
 * <ul>
 *   <li><b>Name</b> – the country name</li>
 *   <li><b>Population</b> – numeric population value (thousand separators allowed)</li>
 *   <li><b>Area (km²)</b> – country area as numeric value (comma or dot decimals allowed)</li>
 * </ul>
 *
 * <p>Any row missing required fields or containing invalid numeric data
 * will be skipped gracefully. A warning is printed to stderr for visibility.</p>
 */
public class CountryService {

    private final CsvTableReader csvReader;
    private final String resourcePath;

    /**
     * Creates a new {@code CountryService}.
     *
     * @param csvReader    the CSV reader used to parse the resource
     * @param resourcePath classpath-relative path to the countries CSV file
     */
    public CountryService(CsvTableReader csvReader, String resourcePath) {
        this.csvReader = csvReader;
        this.resourcePath = resourcePath;
    }

    /**
     * Loads the CSV file specified at construction time and returns the
     * name of the country with the highest population density.
     *
     * <p>Population density is defined as:</p>
     * <pre>
     *     population / area
     * </pre>
     *
     * @return the country name with the highest population density
     * @throws IllegalStateException if the CSV cannot be read or contains no valid rows
     */
    public String findCountryWithHighestPopulationDensity() {
        List<Map<String, String>> rows = loadRows();
        return findCountryWithHighestPopulationDensity(rows);
    }

    /**
     * Internal helper to compute the country with the highest population density
     * from preloaded CSV rows.
     *
     * @param rows list of row maps where each key is a column name
     * @return the name of the densest country
     */
    private String findCountryWithHighestPopulationDensity(List<Map<String, String>> rows) {
        return rows.stream()
                // Convert each row into a Country object or skip invalid rows
                .map(this::toCountrySafely)
                .flatMap(Optional::stream)
                // Find the country with the highest density
                .max(Comparator.comparingDouble(Country::getPopulationDensity))
                .orElseThrow(() -> new IllegalStateException("No valid country rows found"))
                .getName();
    }

    /**
     * Attempts to map a CSV row to a {@link Country} instance.
     * Invalid rows (missing fields, wrong numbers) are logged and skipped.
     *
     * @param row a CSV row
     * @return an {@code Optional<Country>} if parsing succeeds, otherwise empty
     */
    private Optional<Country> toCountrySafely(Map<String, String> row) {
        try {
            // Read required fields
            String name = getRequired(row, "Name");
            String populationStr = getRequired(row, "Population");
            String areaStr = getRequired(row, "Area (km²)");

            // Numeric cleanup: remove thousand separators, unify commas & dots
            long population = Long.parseLong(populationStr.replaceAll("[^0-9]", ""));
            double area = Double.parseDouble(
                    areaStr.replaceAll("[^0-9.,]", "").replace(',', '.')
            );

            return Optional.of(new Country(name, population, area));

        } catch (RuntimeException e) {
            // Provide visibility, but don't break the pipeline
            System.err.println("Skipping invalid country row: " + row + " (" + e.getMessage() + ")");
            return Optional.empty();
        }
    }

    /**
     * Retrieves a required value from a CSV row. Throws an exception if the
     * value is missing or blank.
     *
     * @param row the row map
     * @param key the column key to retrieve
     * @return the trimmed value
     * @throws IllegalArgumentException if the column is missing or empty
     */
    private String getRequired(Map<String, String> row, String key) {
        String value = row.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing column '" + key + "'");
        }
        return value.trim();
    }

    /**
     * Loads all rows from the configured CSV file using the provided
     * {@link CsvTableReader}. Expects the file to be available via
     * the classpath.
     *
     * @return list of parsed CSV rows
     * @throws IllegalStateException if the resource does not exist or cannot be read
     */
    private List<Map<String, String>> loadRows() {
        InputStream input = getClass().getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalStateException("Could not find resource: " + resourcePath);
        }

        try {
            return csvReader.read(input);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading countries CSV file", e);
        }
    }
}
