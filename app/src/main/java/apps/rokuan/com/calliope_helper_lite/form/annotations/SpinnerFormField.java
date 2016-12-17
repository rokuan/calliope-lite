package apps.rokuan.com.calliope_helper_lite.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpinnerFormField {
    String name();
    boolean mandatory() default false;
    String values();
}
