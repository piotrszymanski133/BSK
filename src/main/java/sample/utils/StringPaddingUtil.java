package sample.utils;


public class StringPaddingUtil {
    public static String pad(String input, int multiplicity) {
        StringBuilder builder = new StringBuilder();
        builder.append(input);
        while(builder.length() % 16 != 0) {
            builder.append('0');
        }
        return builder.toString();
    }
}
