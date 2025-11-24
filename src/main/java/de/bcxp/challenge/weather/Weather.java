package de.bcxp.challenge.weather;

import lombok.Value;

/**
 * Domain object representing a single day of weather data.
 */
@Value
public class Weather {

    int day;
    int maxTemperature;
    int minTemperature;

    /**
     * Derived property representing the temperature spread (max - min).
     */
    public int getSpread() {
        return maxTemperature - minTemperature;
    }
}
