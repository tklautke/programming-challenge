package de.bcxp.challenge.countries;

import lombok.Value;

/**
 * Domain object representing a country with its population and land area.
 */
@Value
public class Country {

    String name;
    long population;
    double areaInKm2;

    /**
     * Calculates the population density as people per square kilometer.
     * Returns 0 if area is zero to avoid division-by-zero errors.
     */
    public double getPopulationDensity() {
        return areaInKm2 == 0 ? 0 : population / areaInKm2;
    }
}
