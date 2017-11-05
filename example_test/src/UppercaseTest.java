@Problem(name = "Uppercase")
public class UppercaseTest {
    @EqualityTest
    public EqualityBean helloworld() {
        return new EqualityBean("HELLO WORLD", "hello world");
    }

    @EqualityTest
    public EqualityBean goodbyteworld() {
        return new EqualityBean("GOODBYTE WORLD", "goOdByTe wOrLd");
    }
}
