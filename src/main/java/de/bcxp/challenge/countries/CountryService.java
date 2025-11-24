package de.bcxp.challenge.countries;

import de.bcxp.challenge.util.CsvTableReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This service loads country data from a CSV file
 * and finds the country with the highest population density.
 * Required CSV columns:
 *  - "Name"
 *  - "Population"
 *  - "Area (km²)"
 */
public class CountryService {

    private final CsvTableReader csvReader;
    private final String resourcePath;

    public CountryService(CsvTableReader csvReader, String resourcePath) {
        this.csvReader = csvReader;
        this.resourcePath = resourcePath;
    }

    /**
     * Loads the CSV file and finds the country with the highest population density.
     *
     * @return country name
     * @throws IllegalStateException if no valid rows are found
     */
    public String findCountryWithHighestPopulationDensity() {
        List<Map<String, String>> rows = csvReader.readFromResource(resourcePath);
        List<Country> countries = new ArrayList<>();

        for (Map<String, String> row : rows) {
            Country c = toCountry(row);
            if (c != null) {
                countries.add(c);
            }
        }

        Country best = null;
        for (Country country : countries) {
            if (best == null || country.getPopulationDensity() > best.getPopulationDensity()) {
                best = country;
            }
        }

        if (best == null) {
            throw new IllegalStateException("No valid country rows found");
        }

        return best.getName();
    }

    /**
     * Converts a CSV row into a Country object.
     * Invalid rows are skipped.
     */
    private Country toCountry(Map<String, String> row) {
        try {
            String name = row.get("Name");
            String populationStr = row.get("Population");
            String areaStr = row.get("Area (km²)");

            long population = Long.parseLong(populationStr.replaceAll("[^0-9]", ""));
            double area = Double.parseDouble(areaStr.replaceAll("[^0-9.,]", "").replace(',', '.'));

            return new Country(name, population, area);

        } catch (RuntimeException e) {
            System.err.println("Skipping invalid country row: " + row + " (" + e.getMessage() + ")");
            return null;
        }
    }
}