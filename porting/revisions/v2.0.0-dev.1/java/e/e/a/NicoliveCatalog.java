package e.e.a;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class NicoliveCatalog {
    static final ZoneId JAPAN = ZoneId.of("Asia/Tokyo");
    static final DateTimeFormatter DAY = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String BASE = "https://live.nicovideo.jp";
    private static final int PAGE_SIZE = 70;

    enum Sort {
        NEWEST("開始が新しい順", "recentDesc", (a, b) -> Long.compare(b.begin, a.begin)),
        OLDEST("開始が早い順", "recentAsc", (a, b) -> Long.compare(a.begin, b.begin)),
        VIEWS("来場者数が多い順", "viewCountDesc", (a, b) -> Long.compare(b.views, a.views)),
        COMMENTS("コメント数が多い順", "commentCountDesc", (a, b) -> Long.compare(b.comments, a.comments));

        final String label;
        final String parameter;
        final Comparator<NicoliveData.Program> comparator;

        Sort(String label, String parameter, Comparator<NicoliveData.Program> comparator) {
            this.label = label;
            this.parameter = parameter;
            this.comparator = comparator;
        }

        static Sort restore(String name, Sort fallback) {
            try { return valueOf(name); }
            catch (IllegalArgumentException | NullPointerException ignored) { return fallback; }
        }
    }

    static String recentUrl(int page, Sort sort) {
        return BASE + "/front/api/pages/recent/v1/programs?sortOrder=" + sort.parameter + "&offset=" + page;
    }

    static String timetableUrl(LocalDate date) {
        return BASE + "/timetable?date=" + DAY.format(date);
    }

    static final class Page {
        final List<NicoliveData.Program> programs;
        final boolean more;

        Page(List<NicoliveData.Program> programs, boolean more) {
            this.programs = programs;
            this.more = more;
        }
    }

    static Page recent(String json, int page) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONArray rows = root.getJSONArray("data");
        long total = root.getJSONObject("meta").optLong("totalCount", Long.MAX_VALUE);
        boolean more = rows.length() == PAGE_SIZE && (page + 1L) * PAGE_SIZE < total;
        return new Page(programs(rows), more);
    }

    static List<NicoliveData.Program> timetable(String html, LocalDate date) throws Exception {
        JSONObject root = NicoliveData.embedded(html, "embedded-data", "data-props");
        JSONObject view = root.getJSONObject("view");
        long requested = date.atStartOfDay(JAPAN).toInstant().toEpochMilli();
        if (view.getLong("date") != requested) return Collections.emptyList();
        return programs(view.getJSONArray("programs"));
    }

    private static List<NicoliveData.Program> programs(JSONArray rows) {
        List<NicoliveData.Program> programs = new ArrayList<>(rows.length());
        for (int index = 0; index < rows.length(); index++) {
            JSONObject row = rows.optJSONObject(index);
            NicoliveData.Program program = row == null ? null : NicoliveData.Program.listing(row);
            if (program != null) programs.add(program);
        }
        return programs;
    }
}
