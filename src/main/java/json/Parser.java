package json;

import java.io.*;
import java.util.ArrayDeque;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Parser {

    public static interface Handler {

        void start();

        void end();

        void objectStart();

        void objectEnd();

        void arrayStart();

        void arrayEnd();

        void name(String name);

        void value(String value);

        void value(int value);

        void value(double value);

        void value(boolean value);

        void nullValue();
    }

    public static class JsonParseException extends Exception {

        public JsonParseException() {
            super();
        }

        public JsonParseException(String msg) {
            super(msg);
        }
    }

    private static enum State {
        JSON,
        OBJECT_BEFORE_COLON,
        OBJECT_AFTER_COLON,
        ARRAY,
    }
    
    private InputStream is;

    private Handler handler;

    public Parser(String input, Handler handler) {
        this(new ByteArrayInputStream(input.getBytes()), handler);
    }

    public Parser(InputStream is, Handler handler) {
        this.is = is;
        this.handler = handler;
    }

    public void parse() throws IOException, JsonParseException {
        try (
            Reader _reader = new InputStreamReader(is);
            PushbackReader reader = new PushbackReader(_reader)) {
            int r;
            ArrayDeque<State> state = new ArrayDeque<>();
            state.push(State.JSON);
            handler.start();
            while ((r = reader.read()) != -1) {
                char c = (char) r;
                switch(c) {
                    case '{': {
                        if (state.peek() == State.OBJECT_BEFORE_COLON) throw new JsonParseException();
                        
                        state.push(State.OBJECT_BEFORE_COLON);
                        handler.objectStart();

                        break;
                    }
                    case '}': {
                        
                        state.pop();
                        if (state.peek() == State.OBJECT_BEFORE_COLON) state.pop();
                        handler.objectEnd();

                        break;
                    }

                    case '[': {
                                                state.push(State.ARRAY);
                        handler.arrayStart();

                        break;
                    }
                    case ']': {
                                                if (state.peek() != State.ARRAY) throw new JsonParseException();

                        handler.arrayEnd();
                        state.pop();

                        break;
                    }

                    case 'f': {
                        if (state.peek() == State.OBJECT_BEFORE_COLON) throw new JsonParseException();

                        tryParse(reader, "alse", () -> {
                                                        handler.value(false);
                        });


                        break;
                    }
                    case 't': {
                        if (state.peek() == State.OBJECT_BEFORE_COLON) throw new JsonParseException();

                        tryParse(reader, "rue", () -> {
                                                        handler.value(true);
                        });

                        break;
                    }
                    case 'n': {
                        if (state.peek() == State.OBJECT_BEFORE_COLON) throw new JsonParseException();

                        tryParse(reader, "ull", () -> {
                                                        handler.nullValue();
                        });

                        break;
                    }

                    case '.':  ;
                    case '0':  ;
                    case '1':  ;
                    case '2':  ;
                    case '3':  ;
                    case '4':  ;
                    case '5':  ;
                    case '6':  ;
                    case '7':  ;
                    case '8':  ;
                    case '9':  {
                        if (state.peek() == State.OBJECT_BEFORE_COLON) throw new JsonParseException();
                        reader.unread(c);
                        tryParseNumber(reader, (sb, dotExists) -> {
                            String text = sb.toString();
                            if (dotExists) {
                                                                handler.value(Double.parseDouble(text));
                            } else {
                                                                handler.value(Integer.parseInt(text));
                            }

                            return null;
                        });

                        break;
                    }

                    case '"': {
                        tryParseString(reader, (text) -> {
                            if (state.peek() == State.OBJECT_BEFORE_COLON) {
                                                                handler.name(text.toString());

                            } else {
                                                                handler.value(text.toString());
                            }
                        });

                        break;
                    }

                    case ':': {
                                                if (state.peek() == State.OBJECT_AFTER_COLON) throw new JsonParseException();

                        state.push(State.OBJECT_AFTER_COLON);

                        break;
                    }

                    case ',': {
                                                if (state.peek() == State.ARRAY) break;

                        if (state.peek() == State.OBJECT_BEFORE_COLON) throw new JsonParseException();
                        state.pop();

                        break;
                    }

                    default:
                        if (!Character.isWhitespace(c))
                            throw new JsonParseException("Unexpected [" + c + "]");
                }
            }
                        if (state.peek() != State.JSON) throw new JsonParseException();

            handler.end();
        }
    }

    private static void tryParse(PushbackReader reader, String expecting, Runnable cb) throws IOException, JsonParseException {
        int len = expecting.length();
        char[] buf = new char[len];
        int i;
        if ((i = reader.read(buf, 0, len)) == expecting.length()) {
            if (new String(buf).equals(expecting)) cb.run(); else throw new JsonParseException(); 
        } else {
            throw new JsonParseException();
        }
    }

    private static void tryParseNumber(PushbackReader reader, BiFunction<StringBuilder, Boolean, Void> cb) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder();
        boolean dotExists = false;
        int i;
        while ((i = reader.read()) != -1) {
            char c = (char) i;
            if (Character.isDigit(c)) {
                sb.append(c);
            } else if (c == '.') {
                if (!dotExists) {
                    sb.append(c);
                    dotExists = true;
                } else
                    throw new JsonParseException();
            } else {
                cb.apply(sb, dotExists);
                reader.unread(i);
                return;
            }
        }
        cb.apply(sb, dotExists);
    }

    private static void tryParseString(PushbackReader reader, Consumer<StringBuilder> cb) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = reader.read()) != -1) {
            char c = (char) i;
            if (c != '"') {
                sb.append(c);
            } else {
                cb.accept(sb);
                return;
            }
        }
        throw new JsonParseException();
    }
}
