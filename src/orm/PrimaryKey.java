package orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    int AUTO_INGTEMENT =1;
    int USER_VALUE =2;
    String value();
    int  type() default  1;
}


