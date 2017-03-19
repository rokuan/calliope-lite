package apps.rokuan.com.calliope_helper_lite.util;

/**
 * Created by LEBEAU Christophe on 19/03/2017.
 */

public abstract class SimpleVoidFunction<T> extends SimpleFunction<T, Void> {
    @Override
    public Void apply(T t) {
        process(t);
        return null;
    }

    public abstract void process(T t);
}
