package cum.jesus.cts.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Type {
    protected String name;

    protected Type(String name) {
        this.name = name;
    }

    public abstract int getSize();

    public boolean isIntegerType() {
        return false;
    }

    @Override
    public abstract boolean equals(Object obj);

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    private static final Map<String, Type> namedTypes = new HashMap<>();

    private static final List<Type> types = new ArrayList<>();

    public static void init() {
        namedTypes.put("byte", getIntegerType(8));
        namedTypes.put("short", getIntegerType(16));
        namedTypes.put("int", getIntegerType(32));
        namedTypes.put("long", getIntegerType(64));

        namedTypes.put("void", getVoidType());

        namedTypes.put("string", getStringType());
    }

    public static boolean exists(final String name) {
        return namedTypes.containsKey(name);
    }

    public static Type get(final String name) {
        return namedTypes.get(name);
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

    public static Type getIntegerType(int sizeInBits) {
        for (Type type : types) {
            if (type instanceof IntegerType) {
                if (((IntegerType) type).getSizeInBits() == sizeInBits) {
                    return type;
                }
            }
        }

        types.add(new IntegerType(sizeInBits));
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

    public static Type getFunctionType(Type returnType, final List<Type> args) {
        for (Type type : types) {
            if (type instanceof FunctionType) {
                if (((FunctionType) type).getReturnType().equals(returnType) && ((FunctionType) type).getArgs().equals(args)) {
                    return type;
                }
            }
        }

        types.add(new FunctionType(returnType, args));
        return types.get(types.size() - 1);
    }

    public static Type getPointerType(Type baseType) {
        for (Type type : types) {
            if (type instanceof PointerType) {
                if (((PointerType) type).getUnderlyingType().equals(baseType)) {
                    return type;
                }
            }
        }

        types.add(new PointerType(baseType));
        return types.get(types.size() - 1);
    }
}
