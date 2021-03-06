package oceanebelle.parser.engine.nmea;

import oceanebelle.parser.engine.Parser;
import oceanebelle.parser.engine.nmea.model.NmeaProperty;
import oceanebelle.parser.engine.nmea.parsers.GprmcNmeaParser;
import oceanebelle.parser.engine.nmea.parsers.GpggaNmeaParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point for this library. An engine is a specialised instance
 * that will only hold parsers for which it was configured.
 * The engine takes into account unsupported events if any.
 * Defines which parsers are supported/implemented
 *
 * <pre>
 * How to use:
 * 1. create a builder newBuilder().
 * 2. setup event handlers and error handler for use by builder to create the engine
 * 3. build the engine builder.build().
 * 3. call engine.parse()
 * </pre>
 */
public final class NmeaParserEngineFactory {

    private final static Map<NmeaEvent, Parser<NmeaEvent, NmeaProperty>> parsers;

    static {
        // TODO: Add all supported parsers here. One for every ParseEvent/type
        parsers = new HashMap<NmeaEvent, Parser<NmeaEvent, NmeaProperty>>();

        Parser<NmeaEvent, NmeaProperty> ggaParser = new GpggaNmeaParser();
        parsers.put(ggaParser.getHandledEvent(), ggaParser);

        Parser<NmeaEvent, NmeaProperty> rmcParser = new GprmcNmeaParser();
        parsers.put(rmcParser.getHandledEvent(), rmcParser);
    }

    private NmeaParserEngineFactory() {}

    /**
     * Creates an instance of a builder from which an ParseEngine can be obtained.
     * @return builder
     */
    public static NmeaParserEngineBuilder newBuilder() {
        return new NmeaParserEngineBuilder(parsers, new NmeaEventTranslator());
    }
}
