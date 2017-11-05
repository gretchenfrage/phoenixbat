@Problem(name = "Uppercase", ordinal = 1)
public class UppercaseTest {
    @EqualityTest(ordinal = 2)
    public EqualityBean helloworld() {
        return new EqualityBean("HELLO WORLD", "hello world");
    }

    @EqualityTest(ordinal = 1)
    public EqualityBean goodbyteworld() {
        return new EqualityBean("GOODBYTE WORLD", "goOdByTe wOrLd");
    }

    @EqualityTest(ordinal = 3)
    @Hidden
    public EqualityBean foobar() {
        return new EqualityBean("FOO BAR", "FOO BAR");
    }
}
