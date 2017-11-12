package json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import static json.Event.*;
import static json.Parser.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test public void simpleString() throws IOException, JsonParseException {
        simple("\"bar\"", VALUE("bar"));
    }

    @Test public void simpleInt() throws IOException, JsonParseException {
        simple("654", VALUE(654));
    }

    @Test public void simpleDouble0() throws IOException, JsonParseException {
        simple(".6", VALUE(.6));
    }

    @Test public void simpleDouble1() throws IOException, JsonParseException {
        simple("1.6", VALUE(1.6));
    }

    @Test public void simpleNull() throws IOException, JsonParseException {
        simple("null", NULL);
    }

    @Test public void simpleTrue() throws IOException, JsonParseException {
        simple("true", VALUE(true));
    }

    @Test public void simpleFalse() throws IOException, JsonParseException {
        simple("false", VALUE(false));
    }

    private void simple(String val, Event check) throws IOException, JsonParseException {
        eval(val).expect(
            START,
            check,
            END);
    }

    @Test public void testObjectOnePropString() throws IOException, JsonParseException {
        objectOneProp("\"bar\"", VALUE("bar"));
    }

    @Test public void testObjectOnePropInt() throws IOException, JsonParseException {
        objectOneProp("55", VALUE(55));
    }

    @Test public void testObjectOnePropDouble0() throws IOException, JsonParseException {
        objectOneProp(".9", VALUE(.9));
    }

    @Test public void testObjectOnePropDouble1() throws IOException, JsonParseException {
        objectOneProp("22.33", VALUE(22.33));
    }

    @Test public void testObjectOnePropNull() throws IOException, JsonParseException {
        objectOneProp("null", NULL);
    }

    @Test public void testObjectOnePropTrue() throws IOException, JsonParseException {
        objectOneProp("true", VALUE(true));
    }

    @Test public void testObjectOnePropFalse() throws IOException, JsonParseException {
        objectOneProp("false", VALUE(false));
    }

    private void objectOneProp(String val, Event check) throws IOException, JsonParseException {
        eval(String.format("{ \"foo\": %s }", val)).expect(
            START,
            OBJECT_START,
            NAME("foo"),
            check,
            OBJECT_END,
            END);
    }

    @Test public void objectTwoProps() throws IOException, JsonParseException {
        eval("{ \"foo\": true, \"bar\": \"baz\" }").expect(
            START,
            OBJECT_START,
            NAME("foo"),
            VALUE(true),
            NAME("bar"),
            VALUE("baz"),
            OBJECT_END,
            END);
    }

    @Test public void arrayAllString() throws IOException, JsonParseException {
        eval("[ \"foo\", \"bar\", \"baz\" ]").expect(
            START,
            ARRAY_START,
            VALUE("foo"),
            VALUE("bar"),
            VALUE("baz"),
            ARRAY_END,
            END);
    }

    @Test public void arrayAllInt() throws IOException, JsonParseException {
        eval("[ 1, 2, 3 ]").expect(
            START,
            ARRAY_START,
            VALUE(1),
            VALUE(2),
            VALUE(3),
            ARRAY_END,
            END);
    }

    @Test public void arrayAllDouble() throws IOException, JsonParseException {
        eval("[ .1, 0.2, 1.3 ]").expect(
            START,
            ARRAY_START,
            VALUE(.1),
            VALUE(.2),
            VALUE(1.3),
            ARRAY_END,
            END);
    }

    @Test public void arrayAllBoolean() throws IOException, JsonParseException {
        eval("[ true, false, true ]").expect(
            START,
            ARRAY_START,
            VALUE(true),
            VALUE(false),
            VALUE(true),
            ARRAY_END,
            END);
    }

    @Test public void arrayAllNull() throws IOException, JsonParseException {
        eval("[ null, null ]").expect(
            START,
            ARRAY_START,
            NULL,
            NULL,
            ARRAY_END,
            END);
    }

    @Test public void arrayMixed() throws IOException, JsonParseException {
        eval("[ null, .5, \"foo\", false, true, 77 ]").expect(
            START,
            ARRAY_START,
            NULL,
            VALUE(.5),
            VALUE("foo"),
            VALUE(false),
            VALUE(true),
            VALUE(77),
            ARRAY_END,
            END);
    }

    @Test public void objectInArray() throws IOException, JsonParseException {
        eval("[ {}, {} ]").expect(
            START,
            ARRAY_START,
            OBJECT_START,
            OBJECT_END,
            OBJECT_START,
            OBJECT_END,
            ARRAY_END,
            END);
    }

    @Test public void arrayInArray() throws IOException, JsonParseException {
        eval("[ [], [] ]").expect(
            START,
            ARRAY_START,
            ARRAY_START,
            ARRAY_END,
            ARRAY_START,
            ARRAY_END,
            ARRAY_END,
            END);
    }

    @Test public void arrayInObject() throws IOException, JsonParseException {
        eval("{ \"foo\": [ 1 ] }").expect(
            START,
            OBJECT_START,
            NAME("foo"),
            ARRAY_START,
            VALUE(1),
            ARRAY_END,
            OBJECT_END,
            END);
    }

    @Test public void illegalStringOpenNotClosed0() { illegal("\""); }

    @Test public void illegalStringOpenNotClosed1() { illegal("\"abc"); }

    @Test public void illegalDecimal0() { illegal("0..7"); }

    @Test public void illegalDecimal1() { illegal("..7"); }

    @Test public void illegalObjectOpenNotClosed0() { illegal("{"); }

    @Test public void illegalObjectOpenNotClosed1() { illegal("{ \"foo\": \"bar\""); }

    @Test public void illegalArrayOpenNotClosed() { illegal("["); }

    @Test public void illegalOpenTwice() { illegal("{{ \"a\": 7 }}"); }

    @Test public void illegalObjectStructure0() { illegal("{ \"a\": 7; }"); }

    @Test public void illegalObjectStructure1() { illegal("{ \"a\": 7 : 8 }"); }

    @Test public void illegalPropName0() { illegal("{ a: \"b\" }"); }

    @Test public void illegalPropName1() { illegal("{ 1: \"b\" }"); }

    private  void illegal(String json)  {
        assertThrows(JsonParseException.class, () -> eval(json));
    }

    private TestHandler eval(String json) throws IOException, JsonParseException {
        TestHandler h = new TestHandler();
        new Parser(json, h).parse();
        return h;
    }
}
