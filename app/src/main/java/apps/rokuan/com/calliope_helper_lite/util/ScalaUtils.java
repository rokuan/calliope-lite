package apps.rokuan.com.calliope_helper_lite.util;

import java.util.Arrays;
import java.util.List;

import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.immutable.Map;

/**
 * Created by LEBEAU Christophe on 19/03/2017.
 */

public class ScalaUtils {
    public static <T1, T2> Tuple2<T1, T2> pair(T1 first, T2 second){
        return new Tuple2<T1, T2>(first, second);
    }

    public static <K, V> Map<K, V> asScalaMap(Tuple2<K, ? extends V>... values){
        Object array = new Object[values.length];
        System.arraycopy(array, 0, values, 0, values.length);
        List<Tuple2<K, V>> pairs = Arrays.asList((Tuple2<K, V>)array);
        return Predef.Map().apply(JavaConversions.asScalaBuffer(pairs));
    }
}
