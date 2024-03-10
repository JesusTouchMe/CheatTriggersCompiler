package cum.jesus.cts.ctir.type;

public final class IntegerType extends Type {
    private int sizeInBits;

    IntegerType(int bits) {
        super(1, "i" + bits);

        this.sizeInBits = bits;
    }

    public int getSizeInBits() {
        return sizeInBits;
    }
}
