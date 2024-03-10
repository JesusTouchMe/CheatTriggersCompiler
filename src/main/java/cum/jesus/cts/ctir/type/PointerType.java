package cum.jesus.cts.ctir.type;

public final class PointerType extends Type {
    private Type base;

    PointerType(Type base) {
        super(1, base.name + "*");
        this.base = base;
    }

    public static PointerType get(Type base) {
        return (PointerType) Type.getPointerType(base);
    }

    public Type getBaseType() {
        return base;
    }

    @Override
    public Type getPointerElementType() {
        return base;
    }
}
