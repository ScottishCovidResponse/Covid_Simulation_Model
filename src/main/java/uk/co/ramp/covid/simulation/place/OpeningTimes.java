package uk.co.ramp.covid.simulation.place;

import com.google.gson.*;
import uk.co.ramp.covid.simulation.util.Time;

import java.lang.reflect.Type;
import java.util.BitSet;
import java.util.Objects;

/** This class tracks the opening/closing times of the various places */
public class OpeningTimes {
    private int open;
    private int close;

    // Some places may only be open to visitors during the day, e.g. re-stocking at night
    private int visitorOpen;
    private int visitorClose;

    // 0 index is Monday; 0011111 => weekday only, 1100000 => weekend only etc.
    private BitSet openDays;

    OpeningTimes(int open, int close, int vopen, int vclose, BitSet days) {
        this.open = open;
        this.close = close;
        this.visitorOpen = vopen;
        this.visitorClose = vclose;
        this.openDays = days;
    }

    OpeningTimes(int open, int close, BitSet days) {
        this(open, close, open, close, days);
    }

    public OpeningTimes(int open, int close, int... days) {
        this.openDays = new BitSet();
        for (int d : days) {
            openDays.set(d);
        }
        this.open = open;
        this.close = close;
        this.visitorOpen = open;
        this.visitorClose = close;
    }
    
    public boolean isOpen(Time t) {
        return openDays.get(t.getDay()) && t.getHour() >= open && t.getHour() < close;
    }

    public boolean isOpenToVisitors(Time t) {
        return openDays.get(t.getDay()) && t.getHour() >= visitorOpen && t.getHour() < visitorClose;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public int getVisitorOpen() {
        return visitorOpen;
    }

    public void setVisitorOpen(int visitorOpen) {
        this.visitorOpen = visitorOpen;
    }

    public int getVisitorClose() {
        return visitorClose;
    }

    public void setVisitorClose(int visitorClose) {
        this.visitorClose = visitorClose;
    }

    public BitSet getOpenDays() {
        return openDays;
    }

    public void setOpenDays(BitSet openDays) {
        this.openDays = openDays;
    }

    private static BitSet weekdays;
    private static BitSet weekend;
    private static BitSet allDays;
    
    public static BitSet getWeekdays() {
        if (weekdays == null) {
            weekdays = new BitSet();
            weekdays.set(0, 5);
        }
        return weekdays;
    }
    
    public static BitSet getWeekend() {
        if (weekend == null) {
            weekend = new BitSet();
            weekend.set(5, 7);
        }
        return weekend;
    }

    public static BitSet getAllDays() {
        if (allDays == null) {
            allDays = new BitSet();
            allDays.set(0, 7);
        }
        return allDays;
    }
    
    public static OpeningTimes nineFiveWeekdays() {
      return new OpeningTimes(9,17, OpeningTimes.getWeekdays());
    }

    public static OpeningTimes nineFiveAllWeek() {
        return new OpeningTimes(9,17, OpeningTimes.getAllDays());
    }

    public static OpeningTimes eightTenAllWeek() {
        return new OpeningTimes(8,22, OpeningTimes.getAllDays());
    }

    public static OpeningTimes tenTenAllWeek() {
        return new OpeningTimes(10,22, OpeningTimes.getAllDays());
    }

    public static OpeningTimes twentyfourSeven() {
        return new OpeningTimes(0,24, OpeningTimes.getAllDays());
    }

    public static JsonDeserializer<OpeningTimes> deserializer = (json, typeOfT, context) -> {
        JsonObject o = json.getAsJsonObject();
        int start = o.get("open").getAsInt();
        int end = o.get("close").getAsInt();
        JsonArray daysA = o.get("days").getAsJsonArray();
        int[] days = new Gson().fromJson(daysA, int[].class);

        return new OpeningTimes(start, end, days);
    };

    public static JsonSerializer<OpeningTimes> serializer = (src, typeOfSrc, context) -> {
       JsonObject o = new JsonObject();
       o.addProperty("open", src.open);
       o.addProperty("close", src.close);
       JsonArray oDays = new JsonArray();
       for (int i = 0; i < src.openDays.cardinality(); i++) {
            if (src.openDays.get(i)) {
                oDays.add(i);
            }
       }
       o.add("days", oDays);
       return o;
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningTimes that = (OpeningTimes) o;
        return open == that.open &&
                close == that.close &&
                visitorOpen == that.visitorOpen &&
                visitorClose == that.visitorClose &&
                Objects.equals(openDays, that.openDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, close, visitorOpen, visitorClose, openDays);
    }
}
