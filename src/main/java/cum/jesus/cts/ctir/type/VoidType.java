package cum.jesus.cts.ctir.type;

public final class VoidType extends Type {
    VoidType() {
        super(0, "void");
    }

    @Override
    public boolean isVoidType() {
        return true;
    }
}
