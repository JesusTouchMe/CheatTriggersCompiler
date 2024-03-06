package cum.jesus.cts.type;

public final class StringType extends Type {
    public StringType() {
        super("string");
    }

    @Override
    public int getSize() {
        return 128; // this is only for the string structure which has a 64 bit size and a 64 bit pointer. can vary depending on the vm
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringType;
    }
}
