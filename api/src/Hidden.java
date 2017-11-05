import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * This is a marker for test cases that should be hidden from the student. The input and output will not be visible
 * to the student, but the status will.
 */
public @interface Hidden {
}
