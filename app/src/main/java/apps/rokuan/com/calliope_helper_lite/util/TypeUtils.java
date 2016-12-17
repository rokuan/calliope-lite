package apps.rokuan.com.calliope_helper_lite.util;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class TypeUtils {
    public static boolean isNumericType(Class<?> c){
        /*return Number.class.isAssignableFrom(c)
                || int.class.isAssignableFrom(c)
                || long.class.isAssignableFrom(c);*/
        return Integer.class.isAssignableFrom(c)
                || int.class.isAssignableFrom(c);
    }

    public static boolean isBooleanType(Class<?> c){
        return Boolean.class.isAssignableFrom(c)
                || boolean.class.isAssignableFrom(c);
    }
}
