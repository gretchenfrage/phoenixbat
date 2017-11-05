@Problem(name = "Multiply", ordinal = 4)
public class MultiplyTest {
    @EqualityTest
    public EqualityBean mul5times3() {
        return new EqualityBean(15, 5, 3);
    }
}
