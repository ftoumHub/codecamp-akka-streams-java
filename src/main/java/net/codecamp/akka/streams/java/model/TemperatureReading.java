package net.codecamp.akka.streams.java.model;

import java.time.Instant;
import java.util.UUID;

public final class TemperatureReading {

    // Note: Instances of this class are truly immutable
    // and can only contain valid data
    // that passed the precondition checks in the constructor.

    private final String uuid;

    private final Instant instant;

    private final Float temperature;

    private final TemperatureUnit unit;

    public TemperatureReading(final String uuid,
                              final Instant instant,
                              final Float temperature,
                              final TemperatureUnit unit) {
        // Note: Validation via preconditions.
        // All constraints that concern only the instance under creation itself,
        // i.e. without cross-checks involving other instances, are checked at construction time.
        // In this implementation there is no report of all constraint violations.
        // Instead, if there is a violation, an IllegalArgumentException is raised immediately.
        if (null == uuid || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("uuid must not be null nor empty");
        }
        if (4 != UUID.fromString(uuid).version()) {
            throw new IllegalArgumentException("uuid must be of RFC4122 type 4 random");
        }
        if (null == instant) {
            throw new IllegalArgumentException("instant must not be null");
        }
        if (null == temperature) {
            throw new IllegalArgumentException("temperature must not be null");
        }
        if (null == unit) {
            throw new IllegalArgumentException("unit must not be null");
        }
        this.uuid = uuid;
        this.instant = instant;
        this.temperature = temperature;
        this.unit = unit;
    }

    public String getUuid() {
        return uuid;
    }

    public Instant getInstant() {
        return instant;
    }

    public Float getTemperature() {
        return temperature;
    }

    public TemperatureUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemperatureReading that = (TemperatureReading) o;

        return uuid.equals(that.uuid)
                && instant.equals(that.instant)
                && temperature.equals(that.temperature)
                && unit == that.unit;
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + instant.hashCode();
        result = 31 * result + temperature.hashCode();
        result = 31 * result + unit.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TemperatureReading{" +
                "uuid='" + uuid + '\'' +
                ", instant=" + instant +
                ", temperature=" + temperature +
                ", unit=" + unit +
                '}';
    }

}
