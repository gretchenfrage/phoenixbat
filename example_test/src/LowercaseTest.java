@Problem(name = "Lowercase")
public class LowercaseTest {
    @EqualityTest
    public EqualityBean helloworld() {
        return new EqualityBean("hello world", "HELLo WoRLD");
    }
}
