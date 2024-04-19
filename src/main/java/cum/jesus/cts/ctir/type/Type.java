package cum.jesus.cts.ctir.type;

import java.util.ArrayList;
import java.util.List;

public abstract class Type {
    protected long size;
    protected String name;

    private static List<Type> types = new ArrayList<>();

    protected Type(long size, String name) {
        this.size = size;
        this.name = name;
    }

    /**
     * The size on the VM, not in bits of bytes
     * @return The amount of VM values the type takes up
     */
    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public boolean isIntegerType() {
        return false;
    }

    public boolean isPointerType() {
        return false;
    }

    public boolean isVoidType() {
        return false;
    }

    public boolean isStringType() {
        return false;
    }

    public boolean isStructType() {
        return false;
    }

    public boolean isArrayType() {
        return false;
    }

    public Type getPointerElementType() {
        return this;
    }

    public static Type getVoidType() {
        for (Type type : types) {
            if (type instanceof VoidType) {
                return type;
            }
        }

        types.add(new VoidType());
        return types.get(types.size() - 1);
    }

    public static Type getIntegerType(int bits) {
        for (Type type : types) {
            if (type instanceof IntegerType && type.size == bits) {
                return type;
            }
        }

        types.add(new IntegerType(bits));
        return types.get(types.size() - 1);
    }

    public static Type getFunctionType(Type returnType, List<Type> arguments) {
        for (Type type : types) {
            if (type instanceof FunctionType) {
                if (((FunctionType) type).getReturnType().equals(returnType) && ((FunctionType) type).getArguments().equals(arguments)) {
                    return type;
                }
            }
        }

        types.add(new FunctionType(returnType, arguments));
        return types.get(types.size() - 1);
    }

    public static Type getPointerType(Type base) {
        for (Type type : types) {
            if (type instanceof PointerType && ((PointerType) type).getBaseType().equals(base)) {
                return type;
            }
        }

        types.add(new PointerType(base));
        return types.get(types.size() - 1);
    }

    public static Type getArrayType(Type base, int length) {
        for (Type type : types) {
            if (type instanceof ArrayType && ((ArrayType) type).getBase().equals(base) && ((ArrayType) type).getLength() == length) {
                return type;
            }
        }

        types.add(new ArrayType(base, length));
        return types.get(types.size() - 1);
    }

    public static Type getStringType() {
        for (Type type : types) {
            if (type instanceof StringType) {
                return type;
            }
        }

        types.add(new StringType());
        return types.get(types.size() - 1);
    }

    public static Type getStructType(String name) {
        for (Type type : types) {
            if (type instanceof StructType && type.name.equals(name)) {
                return type;
            }
        }

        types.add(new StructType(name));
        return types.get(types.size() - 1);
    }
}
