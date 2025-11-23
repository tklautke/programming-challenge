package de.bcxp.challenge;

import de.bcxp.challenge.countries.CountryService;
import de.bcxp.challenge.util.CsvTableReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryServiceTest {

    private CountryService countryService;

    @BeforeEach
    void setUp() {
        String testCountriesCsv = "/de/bcxp/challenge/countries.csv";
        CsvTableReader csvReader = new CsvTableReader(';');
        countryService = new CountryService(csvReader, testCountriesCsv);
    }

    @Test
    void findCountryWithHighestPopulationDensity_returnsExpectedCountry() {
        String result = countryService.findCountryWithHighestPopulationDensity();
        assertEquals("Malta", result, "Malta should have the highest population density");
    }

    @Test
    void findCountryWithHighestPopulationDensity_throwsIfNoValidRows() {
        CsvTableReader csvReader = new CsvTableReader(';');
        CountryService emptyService =
                new CountryService(csvReader, "/de/bcxp/challenge/countries_test_empty.csv");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                emptyService::findCountryWithHighestPopulationDensity
        );

        assertEquals("No valid country rows found", ex.getMessage());
    }
}