package json;

class Event {

    public EventType type;

    public Object value;

    public Event(EventType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Event(EventType type) {
        this.type = type;
        this.value = null;
    }

    public static Event START = new Event(EventType.START);

    public static Event END = new Event(EventType.END);

    public static Event OBJECT_START = new Event(EventType.OBJECT_START);

    public static Event OBJECT_END = new Event(EventType.OBJECT_END);

    public static Event ARRAY_START = new Event(EventType.ARRAY_START);

    public static Event ARRAY_END = new Event(EventType.ARRAY_END);

    public static Event NULL = new Event(EventType.VALUE, null);

    public static Event NAME(String name) { return new Event(EventType.NAME, name); }

    public static Event VALUE(int value) { return new Event(EventType.VALUE, value); }

    public static Event VALUE(double value) { return new Event(EventType.VALUE, value); }

    public static Event VALUE(String value) { return new Event(EventType.VALUE, value); }

    public static Event VALUE(boolean value) { return new Event(EventType.VALUE, value); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (type != event.type) return false;
        return value != null ? value.equals(event.value) : event.value == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type + (value == null ? "": "(" + value.toString() + ")");
    }
}
