package e.e.a;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Preserve existing row timestamps while avoiding formatter allocation on every bind. */
public final class RowDates194 {
    private RowDates194() { }
    private static final Pattern TAGS = Pattern.compile("<[^>]*>");
    private static final Pattern EPOCH = Pattern.compile("[0-9]{10}|[0-9]{13}");
    private static final Pattern DATE = Pattern.compile("[0-9]{4}(?:[-/][0-9]{2}[-/][0-9]{2}[ T][0-9]{2}:[0-9]{2}(?::[0-9]{2}(?:\\.[0-9]+)?)?(?:Z|[+-][0-9]{2}:?[0-9]{2})?|年[0-9]{2}月[0-9]{2}日 [0-9]{2}(?:時[0-9]{2}分|:[0-9]{2}))");
    private static final TimeZone ZONE = TimeZone.getTimeZone("Asia/Tokyo");
    private static final String[] PATTERNS = {"yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy年MM月dd日 HH時mm分", "yyyy年MM月dd日 HH:mm"};
    private static final ThreadLocal<Formats> FORMATS = new ThreadLocal<>();
    private static final Map<String, Long> PARSED = new LinkedHashMap<String, Long>(256, .75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, Long> entry) { return size() > 256; }
    };
    private static final class Formats {
        final SimpleDateFormat[] input = new SimpleDateFormat[PATTERNS.length];
        final SimpleDateFormat output = create("yyyy/MM/dd HH:mm");
        SimpleDateFormat get(int i) { if (input[i] == null) input[i] = create(PATTERNS[i]); return input[i]; }
    }
    private static SimpleDateFormat create(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ROOT);
        format.setLenient(false); format.setTimeZone(ZONE); return format;
    }
    private static Formats formats() {
        Formats formats = FORMATS.get();
        if (formats == null) { formats = new Formats(); FORMATS.set(formats); }
        return formats;
    }
    public static long millis(String source) {
        if (source == null) return 0;
        if (source.length() <= 2048) synchronized (PARSED) { Long cached = PARSED.get(source); if (cached != null) return cached; }
        long value = parse(source);
        if (source.length() <= 2048) synchronized (PARSED) { PARSED.put(source, value); }
        return value;
    }
    private static long parse(String source) {
        source = TAGS.matcher(source).replaceAll("").trim();
        if (EPOCH.matcher(source).matches()) try { long value = Long.parseLong(source); return source.length() == 10 ? value * 1000 : value; }
        catch (NumberFormatException ignored) { return 0; }
        Matcher match = DATE.matcher(source); if (!match.find()) return 0;
        String date = match.group(); Formats formats = formats();
        for (int i = 0; i < PATTERNS.length; i++) try {
            SimpleDateFormat format = formats.get(i);
            // Explicit zones parsed by SimpleDateFormat may change its Calendar zone.
            format.setTimeZone(ZONE);
            Date parsed = format.parse(date); if (parsed != null) return parsed.getTime();
        } catch (java.text.ParseException ignored) { }
        return 0;
    }
    public static String format(long millis, String label) {
        if (millis <= 0) return "";
        return formats().output.format(new Date(millis)) + " " + UiStrings.translate(label);
    }
}
