import java.util.function.Predicate;

public final class AcceptanceBean {
    public Object[] args;
    public Predicate<Object> test;

    public AcceptanceBean(Predicate<Object> test, Object... args) {
        this.args = args;
        this.test = test;
    }
}
