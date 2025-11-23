package de.bcxp.challenge;

import de.bcxp.challenge.util.CsvTableReader;
import de.bcxp.challenge.weather.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        String testWeatherCsv = "/de/bcxp/challenge/weather_test.csv";
        CsvTableReader csvReader = new CsvTableReader(',');
        weatherService = new WeatherService(csvReader, testWeatherCsv);
    }

    @Test
    void findDayWithSmallestTemperatureSpread_returnsExpectedDay() {
        int result = weatherService.findDayWithSmallestTemperatureSpread();
        assertEquals(2, result, "Day 2 should have the smallest temperature spread");
    }

    @Test
    void findDayWithSmallestTemperatureSpread_throwsIfNoValidRows() {
        CsvTableReader csvReader = new CsvTableReader(',');
        WeatherService emptyService =
                new WeatherService(csvReader, "/de/bcxp/challenge/weather_test_empty.csv");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                emptyService::findDayWithSmallestTemperatureSpread
        );

        assertEquals("No valid weather rows found", ex.getMessage());
    }
}