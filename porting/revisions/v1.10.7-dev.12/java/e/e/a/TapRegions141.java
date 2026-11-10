package e.e.a;

/** Percent ranges for the control toggle. Overlap is evaluated as a union. */
public final class TapRegions141 {
    static final int ALL = 0, EDGES = 1, CENTER = 2, CUSTOM = 3;
    static final int LIMIT = 3;
    private static final String[] MODES = {"all", "edges", "center", "custom"};

    final int mode;
    final int[][] ranges;

    private TapRegions141(int mode, int[][] ranges) {
        this.mode = mode;
        this.ranges = ranges;
    }

    static TapRegions141 preset(int mode) {
        if (mode == EDGES) return new TapRegions141(EDGES, new int[][]{{0, 15}, {85, 100}});
        if (mode == CENTER) return new TapRegions141(CENTER, new int[][]{{30, 70}});
        return new TapRegions141(ALL, new int[][]{{0, 100}});
    }

    static TapRegions141 custom(int[][] ranges) {
        if (ranges.length < 1 || ranges.length > LIMIT) return preset(ALL);
        int[][] copy = new int[ranges.length][2];
        for (int i = 0; i < ranges.length; i++) {
            int start = ranges[i][0], end = ranges[i][1];
            if (start < 0 || end > 100 || start >= end) return preset(ALL);
            copy[i][0] = start;
            copy[i][1] = end;
        }
        return new TapRegions141(CUSTOM, copy);
    }

    static TapRegions141 parse(String value) {
        if (value == null || value.length() > 48) return preset(ALL);
        for (int i = 0; i < CUSTOM; i++) if (MODES[i].equals(value)) return preset(i);
        if (!value.startsWith("custom:")) return preset(ALL);
        try {
            String[] items = value.substring(7).split(",", -1);
            if (items.length > LIMIT) return preset(ALL);
            int[][] result = new int[items.length][2];
            for (int i = 0; i < items.length; i++) {
                if (!items[i].matches("[0-9]{1,3}-[0-9]{1,3}")) return preset(ALL);
                int split = items[i].indexOf('-');
                result[i][0] = Integer.parseInt(items[i].substring(0, split));
                result[i][1] = Integer.parseInt(items[i].substring(split + 1));
            }
            return custom(result);
        } catch (NumberFormatException ignored) {
            return preset(ALL);
        }
    }

    String encode() {
        if (mode != CUSTOM) return MODES[mode];
        StringBuilder value = new StringBuilder("custom:");
        for (int i = 0; i < ranges.length; i++) {
            if (i > 0) value.append(',');
            value.append(ranges[i][0]).append('-').append(ranges[i][1]);
        }
        return value.toString();
    }

    boolean contains(float x, float width, float minimumWidth) {
        if (!(width > 0 && x >= 0 && x <= width)) return false;
        float minimum = Math.min(minimumWidth, width * .25f);
        for (int[] range : ranges) {
            float start = width * range[0] / 100f;
            float end = width * range[1] / 100f;
            if (end - start < minimum) {
                start = Math.max(0, Math.min(width - minimum, (start + end - minimum) / 2));
                end = start + minimum;
            }
            if (x >= start && x <= end) return true;
        }
        return false;
    }
}
