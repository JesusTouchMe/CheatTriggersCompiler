package cum.jesus.cts.type;

public final class IntegerType extends Type {
    private final int sizeInBits;

    public IntegerType(int sizeInBits) {
        super("i" + sizeInBits);
        this.sizeInBits = sizeInBits;
    }

    public int getSizeInBits() {
        return sizeInBits;
    }

    @Override
    public int getSize() {
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

    public static IntegerType get(int sizeInBits) {
        return (IntegerType) Type.getIntegerType(sizeInBits);
    }
}
