@Problem(name = "Square")
public class SquareTest {
    @EqualityTest
    public EqualityBean square2() {
        return new EqualityBean(4, 2);
    }

    @EqualityTest
    public EqualityBean square5() {
        return new EqualityBean(25, 5);
    }
}
