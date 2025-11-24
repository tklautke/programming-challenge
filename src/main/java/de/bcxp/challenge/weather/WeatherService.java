package de.bcxp.challenge.weather;

import de.bcxp.challenge.util.CsvTableReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This service loads weather data from a CSV file
 * and finds the day with the smallest temperature spread.
 * The CSV file must contain at least these columns:
 *  - "Day"
 *  - "MxT" (max temperature)
 *  - "MnT" (min temperature)
 */
public class WeatherService {

    private final CsvTableReader csvReader;
    private final String resourcePath;

    public WeatherService(CsvTableReader csvReader, String resourcePath) {
        this.csvReader = csvReader;
        this.resourcePath = resourcePath;
    }

    /**
     * Loads the CSV file and finds the day with the smallest temperature spread.
     *
     * @return the day as integer
     * @throws IllegalStateException if no valid data rows are found
     */
    public int findDayWithSmallestTemperatureSpread() {
        List<Map<String, String>> rows = csvReader.readFromResource(resourcePath);

        List<Weather> weatherDays = new ArrayList<>();
        for (Map<String, String> row : rows) {
            Weather weather = toWeather(row);
            if (weather != null) {
                weatherDays.add(weather);
            }
        }

        Weather best = null;
        for (Weather day : weatherDays) {
            if (best == null || day.getSpread() < best.getSpread()) {
                best = day;
            }
        }

        if (best == null) {
            // must remain exact for unit tests
            throw new IllegalStateException("No valid weather rows found");
        }

        return best.getDay();
    }

    /**
     * Converts a CSV row into a Weather object.
     * Invalid rows are skipped.
     */
    private Weather toWeather(Map<String, String> row) {
        try {
            int day = Integer.parseInt(row.get("Day"));
            int max = Integer.parseInt(row.get("MxT"));
            int min = Integer.parseInt(row.get("MnT"));

            return new Weather(day, max, min);

        } catch (RuntimeException e) {
            System.err.println("Skipping invalid weather row: " + row + " (" + e.getMessage() + ")");
            return null;
        }
    }
}