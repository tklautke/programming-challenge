package de.bcxp.challenge;

import de.bcxp.challenge.countries.CountryService;
import de.bcxp.challenge.util.CsvTableReader;
import de.bcxp.challenge.weather.WeatherService;

/**
 * The entry class for your solution. This class stays minimal and only wires up
 * the components you created in your clean architecture.
 */
public final class App {

    private static final String WEATHER_CSV = "/de/bcxp/challenge/weather.csv";
    private static final String COUNTRIES_CSV = "/de/bcxp/challenge/countries.csv";

    /**
     * Main entry method.
     *
     * @param args CLI arguments
     */
    public static void main(String... args) {

        // --- Infrastructure setup ---
        CsvTableReader weatherCsvReader = new CsvTableReader(',');
        CsvTableReader countryCsvReader = new CsvTableReader(';');


        // --- Application services ---
        WeatherService weatherService = new WeatherService(weatherCsvReader, WEATHER_CSV);
        CountryService countryService = new CountryService(countryCsvReader, COUNTRIES_CSV);

        // --- Your analysis calls ---
        String dayWithSmallestTempSpread =
                String.valueOf(weatherService.findDayWithSmallestTemperatureSpread());

        String countryWithHighestPopulationDensity =
                 countryService.findCountryWithHighestPopulationDensity();

        // --- Output ---
        System.out.printf("Day with smallest temperature spread: %s%n", dayWithSmallestTempSpread);
        System.out.printf("Country with highest population density: %s%n", countryWithHighestPopulationDensity);
    }
}
