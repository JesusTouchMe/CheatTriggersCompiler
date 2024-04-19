package cum.jesus.cts.ctir.type;

public final class ArrayType extends Type {
    private Type base;
    private int length;

    ArrayType(Type base, int length) {
        super(base.getSize() * length, base.name + "[" + length + "]");

        this.base = base;
        this.length = length;
    }

    public static ArrayType get(Type base, int length) {
        return (ArrayType) Type.getArrayType(base, length);
    }

    public Type getBase() {
        return base;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean isArrayType() {
        return true;
    }

    @Override
    public Type getPointerElementType() {
        return base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PointerType && ((PointerType) o).getBaseType().equals(base)) return true;
        return o instanceof ArrayType && ((ArrayType) o).base.equals(base) && ((ArrayType) o).length == length;
    }
}
