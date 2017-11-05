@Problem(name = "RandWithinTen", ordinal = 3)
public class RandWithinTenTest {

    @AcceptanceTest
    public AcceptanceBean test() {
        return new AcceptanceBean(obj -> {
            if (obj instanceof Integer) {
                int n = (Integer) obj;
                return n >= 0 && n < 10;
            } else return false;
        });
    }

}
