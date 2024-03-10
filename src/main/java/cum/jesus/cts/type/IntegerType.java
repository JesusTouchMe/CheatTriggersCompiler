package cum.jesus.cts.type;

import cum.jesus.cts.ctir.type.Type;

public final class IntegerType extends cum.jesus.cts.type.Type {
    private final int sizeInBits;

    public IntegerType(int sizeInBits) {
        super(Type.getIntegerType(sizeInBits));
        this.sizeInBits = sizeInBits;
    }

    public int getSizeInBits() {
        return sizeInBits;
    }

    @Override
    public boolean isIntegerType() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntegerType && ((IntegerType) obj).sizeInBits <= this.sizeInBits;
    }
}
