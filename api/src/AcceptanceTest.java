import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * This is a test annotation for problems which may have a variety of correct outputs, or for which outputs cannot be
 * checked by the .equals method. These methods are of the signature () => AcceptanceBean.
 */
public @interface AcceptanceTest {

    int ordinal() default 0;

}
