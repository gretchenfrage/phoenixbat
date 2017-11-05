public final class EqualityBean {
    public Object[] args;
    public Object expected;

    public EqualityBean(Object expected, Object... args) {
        this.args = args;
        this.expected = expected;
    }
}
