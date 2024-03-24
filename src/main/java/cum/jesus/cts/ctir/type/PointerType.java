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
    public boolean isPointerType() {
        return true;
    }

    @Override
    public Type getPointerElementType() {
        return base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ArrayType && ((ArrayType) o).getBase().equals(base)) return true;
        return o instanceof PointerType && ((PointerType) o).base.equals(base);
    }
}
