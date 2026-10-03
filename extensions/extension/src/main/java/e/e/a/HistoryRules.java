package e.e.a;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HistoryRules {
    private HistoryRules() { }
    private static final Pattern ID = Pattern.compile("(?:^|/(?:watch|shorts)/)((?:sm|nm|so|ss)?[0-9]+)(?:[/?#].*)?$");
    public static String id(String text) {
        if (text == null) return null;
        Matcher match = ID.matcher(text.trim());
        return match.find() ? match.group(1) : text;
    }
    public static boolean same(String first, Object second) {
        return first != null && second instanceof String && id(first).equals(id((String) second));
    }
    public static boolean contains(Set<?> selected, Object stored) {
        for (Object value : selected) if (value instanceof String && same((String) value, stored)) return true;
        return false;
    }
}
