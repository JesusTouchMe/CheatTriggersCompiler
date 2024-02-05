package cum.jesus.cts.type;

/**
 * Invisible type used by compiler and vm to access memory for objects and more
 * This type won't be usable for the language
 */
public final class PointerType extends Type {
    private Type baseType;

    public PointerType(Type baseType) {
        super(baseType.name + "*");
        this.baseType = baseType;
    }

    public Type getUnderlyingType() {
        return baseType;
    }

    @Override
    public int getSize() {
        return 64;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PointerType)) {
            return false;
        }

        return ((PointerType) obj).baseType.equals(baseType);
    }

    public static PointerType get(Type baseType) {
        return (PointerType) Type.getPointerType(baseType);
    }
}
