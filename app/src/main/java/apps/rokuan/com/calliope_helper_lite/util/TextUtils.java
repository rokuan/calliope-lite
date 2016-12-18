package apps.rokuan.com.calliope_helper_lite.util;

/**
 * Created by LEBEAU Christophe on 18/12/2016.
 */

public class TextUtils {
    public static String toLabel(String s){
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).replaceAll("([A-Z])", " $1");
    }
}
