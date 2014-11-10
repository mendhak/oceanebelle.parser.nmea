package oceanebelle.parser.engine.nmea.parsers;

import oceanebelle.parser.engine.ParseException;
import oceanebelle.parser.engine.nmea.model.*;
import oceanebelle.parser.engine.nmea.NmeaEvent;
import oceanebelle.parser.engine.ParserHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class GpggaNmeaParserTest {

    @Captor
    private ArgumentCaptor<Map<NmeaProperty, Object>> adapterCaptor;

    @Mock
    private ParserHandler<NmeaEvent, NmeaProperty> handler;

    private GpggaNmeaParser parser;


    @Before
    public void setup() {
        when(handler.getHandledEvent()).thenReturn(NmeaEvent.GPGGA);
        parser = new GpggaNmeaParser();
    }

    @Test(expected = ParseException.class)
    public void whenParsingInvalidPattenThenThrowException() throws ParseException {
        String sentence = "$GPGGA,123519,8,0.9,545.4,M,46.9,M,,*47";

        parser.parse(sentence, handler);


    }

    @Test
    public void whenParsingInvalidChecksum() throws ParseException {
        String sentence = "$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*49";

        parser.parse(sentence, handler);

        verify(handler).handle(adapterCaptor.capture());

        NmeaDataAdapter adapter = new NmeaDataAdapter(adapterCaptor.getValue());

        assertEquals(12, adapter.getDateTimeData().getRawTime().getHour());
        assertEquals(35, adapter.getDateTimeData().getRawTime().getMin());
        assertEquals(19, adapter.getDateTimeData().getRawTime().getSec());

        // TODO: The data will still be read, should processing be skipped?
        assertFalse(adapter.isChecksumValid());

    }

    @Test
    public void whenParsingValidPattern1() throws ParseException {
        String sentence = "$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47";

        parser.parse(sentence, handler);

        verify(handler).handle(adapterCaptor.capture());

        NmeaDataAdapter adapter = new NmeaDataAdapter(adapterCaptor.getValue());

        assertCommonNmeaData(adapter);

        // Assert values
        assertEquals(Coordinates.of("4807.038","N","01131.000","E"), adapter.getCoordinates());
        assertEquals(Integer.valueOf(8), adapter.getSatellites());
        assertEquals(FixQuality.GPS_SPS, adapter.getFixQuality());

        assertEquals(12, adapter.getDateTimeData().getRawTime().getHour());
        assertEquals(35, adapter.getDateTimeData().getRawTime().getMin());
        assertEquals(19, adapter.getDateTimeData().getRawTime().getSec());

        assertEquals(545.4f, adapter.getAltitude());

    }

    @Test
    public void whenParsingValidPattern2() throws ParseException {
        String sentence = "$GPGGA,104433.591,5920.7019,N,01803.2940,E,1,04,8.9,77.8,M,23.2,M,0.0,0000*44";

        parser.parse(sentence, handler);

        verify(handler).handle(adapterCaptor.capture());

        NmeaDataAdapter adapter = new NmeaDataAdapter(adapterCaptor.getValue());

        assertCommonNmeaData(adapter);

        // Assert values
        assertEquals(Coordinates.of("5920.7019","N","01803.2940","E"), adapter.getCoordinates());
        assertEquals(Integer.valueOf(4), adapter.getSatellites());
        assertEquals(FixQuality.GPS_SPS, adapter.getFixQuality());

        assertEquals(10, adapter.getDateTimeData().getRawTime().getHour());
        assertEquals(44, adapter.getDateTimeData().getRawTime().getMin());
        assertEquals(33, adapter.getDateTimeData().getRawTime().getSec());

        assertEquals(77.8f, adapter.getAltitude());

    }

    @Test
    public void whenParsingValidPattern3() throws ParseException {
        String sentence = "$GPGGA,103748,5121.578305,N,00011.515493,W,2,09,0.8,85.0,M,47.0,M,,*61";

        parser.parse(sentence, handler);

        verify(handler).handle(adapterCaptor.capture());

        NmeaDataAdapter adapter = new NmeaDataAdapter(adapterCaptor.getValue());

        assertEquals(10, adapter.getDateTimeData().getRawTime().getHour());
        assertEquals(37, adapter.getDateTimeData().getRawTime().getMin());
        assertEquals(48, adapter.getDateTimeData().getRawTime().getSec());
        assertCommonNmeaData(adapter);
    }

    @Test
    public void whenParsingEmptyPattern() throws ParseException {
        String sentence = "$GPGGA,,,,,,0,,,,,,,,*66";

        parser.parse(sentence, handler);

        verify(handler, times(0)).handle(anyMapOf(NmeaProperty.class, Object.class));

    }

    private void assertCommonNmeaData(NmeaDataAdapter adapter) {
        assertTrue(adapter.isChecksumValid());
        assertTrue(adapter.hasProperties(
                NmeaProperty.Type,
                NmeaProperty.IsValidChecksum,
                NmeaProperty.Coordinates,
                NmeaProperty.Satellites,
                NmeaProperty.FixQuality,
                NmeaProperty.Altitude));

        assertEquals(NmeaEvent.GPGGA.name(), adapter.getProperty(NmeaProperty.Type, String.class));

        // not set
        assertFalse(adapter.hasProperties(NmeaProperty.Speed));


        assertNull(adapter.getSpeed());
    }
}
