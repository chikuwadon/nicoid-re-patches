package e.e.a;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Iterator;
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

    enum Broadcast {
        LIVE("放送中", "onair", "ON_AIR"),
        UPCOMING("放送予定", "comingsoon", "RELEASED"),
        ENDED("放送終了", "closed", "ENDED");

        final String label;
        final String parameter;
        final String state;

        Broadcast(String label, String parameter, String state) {
            this.label = label;
            this.parameter = parameter;
            this.state = state;
        }

        boolean matches(NicoliveData.Program program) {
            return state.equals(program.status);
        }
    }

    static String rankingUrl(Broadcast state) {
        return BASE + "/ranking?type=" + state.parameter;
    }

    static String followUrl(Broadcast state) {
        return BASE + "/follow" + (state == Broadcast.LIVE ? "" : "?status=" + state.parameter);
    }

    static List<NicoliveData.Program> ranking(String html, Broadcast state, boolean user) throws Exception {
        JSONObject root = NicoliveData.embedded(html, "embedded-data", "data-props").getJSONObject("ranking");
        if (!state.parameter.equals(root.getString("statusCategory"))) throw new JSONException("RANKING_STATUS_MISMATCH");
        JSONArray items = root.getJSONArray(user ? "userPrograms" : "officialAndChannelPrograms");
        LinkedHashMap<String, NicoliveData.Program> ranked = new LinkedHashMap<>();
        for (int index = 0; index < items.length(); index++) {
            JSONObject item = items.optJSONObject(index);
            JSONObject row = item == null ? null : item.optJSONObject("value");
            NicoliveData.Program program = row == null ? null : NicoliveData.Program.listing(row);
            if (program != null && state.matches(program)) ranked.putIfAbsent(program.id, program);
        }
        return new ArrayList<>(ranked.values());
    }

    static List<NicoliveData.Program> followed(String html) throws Exception {
        JSONObject root;
        try {
            root = NicoliveData.embedded(html, "embedded-data", "data-props");
        } catch (java.io.IOException missing) {
            root = NicoliveData.embedded(html, "DAT-csr-data", "data-value").getJSONObject("props");
        }
        if (!root.getJSONObject("user").optBoolean("isLoggedIn")) return null;
        JSONObject view = root.getJSONObject("view");
        LinkedHashMap<String, NicoliveData.Program> programs = new LinkedHashMap<>();
        for (Iterator<String> keys = view.keys(); keys.hasNext();) {
            String key = keys.next();
            String name = key.toLowerCase(java.util.Locale.ROOT);
            if (name.contains("programlist") && !name.contains("recommend") && !name.contains("popular")) {
                NicoliveData.collect(view.opt(key), programs, 0);
            }
        }
        if (programs.isEmpty() && view.optJSONObject("followProgramListSectionState") == null) {
            JSONObject domain = root.optJSONObject("follow");
            if (domain != null) NicoliveData.collect(domain, programs, 0);
        }
        return new ArrayList<>(programs.values());
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
        boolean more = rows.length() > 0 && (total == Long.MAX_VALUE
                ? rows.length() >= PAGE_SIZE && page < 99 : (page + 1L) * PAGE_SIZE < total);
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
