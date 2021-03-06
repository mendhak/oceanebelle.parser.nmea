package oceanebelle.parser.engine.nmea.model;

/**
 * All properties that may be present in a key-value dictionary
 */
public enum NmeaProperty {
    Type,
    Coordinates,
    FixQuality,
    Satellites,
    Altitude,
    Speed,
    DateTimeData, IsValidChecksum,
}
