import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Each problem should be represented as a class which is given this annotation. Its tests are annotated with test
 * annotations.
 */
public @interface Problem {

    String name();

}


