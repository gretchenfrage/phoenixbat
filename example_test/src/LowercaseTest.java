@Problem(name = "Lowercase", ordinal = 5)
public class LowercaseTest {
    @EqualityTest
    public EqualityBean helloworld() {
        return new EqualityBean("hello world", "HELLo WoRLD");
    }
}
