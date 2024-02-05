package cum.jesus.cts.type;

public final class VoidType extends Type {
    public VoidType() {
        super("void");
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }
}
