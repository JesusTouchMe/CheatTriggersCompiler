package cum.jesus.cts.util;

public final class NumberUtils {
    public static int parseInt(String str) {
        if (str.startsWith("0x")) {
            return Integer.parseInt(str.substring(2), 16);
        } else if (str.startsWith("0b")) {
            return Integer.parseInt(str.substring(2), 2);
        } else if (str.startsWith("0")) {
            return Integer.parseInt(str, 8);
        } else {
            return Integer.parseInt(str);
        }
    }

    public static long parseLong(String str) {
        if (str.startsWith("0x")) {
            return Long.parseLong(str.substring(2), 16);
        } else if (str.startsWith("0b")) {
            return Long.parseLong(str.substring(2), 2);
        } else if (str.startsWith("0")) {
            return Long.parseLong(str, 8);
        } else {
            return Long.parseLong(str);
        }
    }
}
