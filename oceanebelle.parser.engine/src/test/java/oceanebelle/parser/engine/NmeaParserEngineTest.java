package oceanebelle.parser.engine;

import oceanebelle.parser.engine.nmea.NmeaEvent;
import oceanebelle.parser.engine.nmea.model.NmeaProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by oceanebelle on 09/11/14.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class NmeaParserEngineTest {

    @Mock
    private Parser<NmeaEvent, NmeaProperty> mockGpapbParser;
    @Mock
    private ParserHandler<NmeaEvent, NmeaProperty> mockGpapbHandler;
    @Mock
    private Translator<NmeaEvent> mockTranslator;

    private Map<NmeaEvent, Parser<NmeaEvent, NmeaProperty>> parsers = new HashMap<NmeaEvent, Parser<NmeaEvent, NmeaProperty>>();
    private Map<NmeaEvent, ParserHandler<NmeaEvent, NmeaProperty>> handlers = new HashMap<NmeaEvent, ParserHandler<NmeaEvent, NmeaProperty>>();

    private ParserEngine engine;


    @Before
    public void setup() {

        when(mockTranslator.translate(NmeaEvent.GPAPB.name())).thenReturn(NmeaEvent.GPAPB);
        parsers.put(NmeaEvent.GPAPB, mockGpapbParser);
        handlers.put(NmeaEvent.GPAPB, mockGpapbHandler);

        engine = getEngine(parsers, handlers, mockTranslator, null);
    }

    public abstract ParserEngine getEngine(Map<NmeaEvent, Parser<NmeaEvent, NmeaProperty>> parsers, Map<NmeaEvent, ParserHandler<NmeaEvent, NmeaProperty>> handlers, Translator<NmeaEvent> mockTranslator, ErrorHandler handler);

    @Test
    public void whenHasValidSentencesInvokeParsers() throws ParseException {
        InputStream data = setupData(NmeaEvent.GPAPB);
        engine.parse(data);
        verify(mockGpapbParser, times(1)).parse(eq(NmeaEvent.GPAPB.name()), eq(mockGpapbHandler));
    }

    @Test
    public void whenProcessingIsSlowInvokeParsersTillEnd() throws ParseException {
        InputStream data = setupData(NmeaEvent.GPAPB, NmeaEvent.GPAPB);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(100L);
                return null;
            }
        }).when(mockGpapbParser).parse(eq(NmeaEvent.GPAPB.name()), eq(mockGpapbHandler));

        engine.parse(data);
        verify(mockGpapbParser, times(2)).parse(eq(NmeaEvent.GPAPB.name()), eq(mockGpapbHandler));
    }

    private InputStream setupData(NmeaEvent ... events) {
        StringBuilder sb = new StringBuilder();
        for (NmeaEvent event : events) {
            sb.append(event).append("\n");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
