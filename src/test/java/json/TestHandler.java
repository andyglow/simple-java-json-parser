package json;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestHandler implements Parser.Handler {

    private ArrayList<Event> events = new ArrayList<>();

    public void expect(Event ...expected) {
        assertEquals(Arrays.asList(expected), events);
    }

    @Override
    public void start() { events.add(Event.START); }

    @Override
    public void end() { events.add(Event.END); }

    @Override
    public void objectStart() { events.add(Event.OBJECT_START); }

    @Override
    public void objectEnd() { events.add(Event.OBJECT_END); }

    @Override
    public void arrayStart() { events.add(Event.ARRAY_START); }

    @Override
    public void arrayEnd() { events.add(Event.ARRAY_END); }

    @Override
    public void name(String name) { events.add(Event.NAME(name)); }

    @Override
    public void value(String value) { events.add(Event.VALUE(value)); }

    @Override
    public void value(int value) { events.add(Event.VALUE(value)); }

    @Override
    public void value(double value) { events.add(Event.VALUE(value)); }

    @Override
    public void value(boolean value) { events.add(Event.VALUE(value)); }

    @Override
    public void nullValue() { events.add(Event.NULL); }
}
