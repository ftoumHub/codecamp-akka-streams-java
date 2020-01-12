package net.codecamp.akka.streams.java.model;

import java.util.HashMap;
import java.util.Map;

public enum TemperatureUnit {

    DEGREES_CELSIUS("C"),

    DEGREES_FAHRENHEIT("F"),

    KELVIN("K");

    private static final Map<String, TemperatureUnit> map;

    static {
        final Map<String, TemperatureUnit> initialMap = new HashMap<>(3);
        initialMap.put(DEGREES_CELSIUS.letter, DEGREES_CELSIUS);
        initialMap.put(DEGREES_FAHRENHEIT.letter, DEGREES_FAHRENHEIT);
        initialMap.put(KELVIN.letter, KELVIN);
        map = initialMap;
    }

    private final String letter;

    // private enum constructor does not require private keyword
    TemperatureUnit(final String letter) {
        this.letter = letter;
    }

    public static TemperatureUnit fromLetter(final String letter) {
        if (null == letter || letter.trim().isEmpty() || letter.length() != 1) {
            throw new IllegalArgumentException("letter must contain exactly one character");
        }
        TemperatureUnit temperatureUnit = map.get(letter);
        if (null == temperatureUnit) {
            throw new IllegalArgumentException("unsupported letter " + letter);
        }
        return temperatureUnit;
    }

}
