package cum.jesus.cts.type;

import cum.jesus.cts.ctir.type.PointerType;

public final class ArrayType extends Type {
    private Type base;
    private boolean reference;

    public ArrayType(Type base, int length, boolean reference) {
        super(reference ? PointerType.get(base.getIRType()) : cum.jesus.cts.ctir.type.ArrayType.get(base.getIRType(), length));

        this.base = base;
        this.reference = reference;
    }

    @Override
    public boolean isArrayType() {
        return true;
    }

    @Override
    public Type getBase() {
        return base;
    }

    public boolean isReference() {
        return reference;
    }
}
