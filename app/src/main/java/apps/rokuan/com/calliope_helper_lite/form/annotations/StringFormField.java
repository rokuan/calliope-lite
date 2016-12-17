package apps.rokuan.com.calliope_helper_lite.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LEBEAU Christophe on 16/12/2016.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StringFormField {
    int label() default -1;
    boolean mandatory() default false;
    String matches() default "";
    int minLength() default 0;
    int maxLength() default Integer.MAX_VALUE;
}
