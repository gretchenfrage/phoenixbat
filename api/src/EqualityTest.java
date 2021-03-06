import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * This is a test annotation for problems which have a single correct output. These methods are of the signature
 * () => EqualityBean.
 */
public @interface EqualityTest {

    int ordinal() default 0;

}
