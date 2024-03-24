package cum.jesus.cts.ctir.type;

public final class StringType extends Type {
    StringType() {
        super(1, "string");
    }

    @Override
    public boolean isStringType() {
        return true;
    }
}
