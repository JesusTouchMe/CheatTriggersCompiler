package cum.jesus.cts.util;

public final class CharUtils {
    public static boolean isXDigit(char ch) {
        return Character.toString(ch).matches("[0-9A-Fa-f]");
    }
}
