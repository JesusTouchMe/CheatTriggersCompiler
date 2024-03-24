package cum.jesus.cts.type;

import cum.jesus.cts.ctir.type.ArrayType;

import java.util.HashMap;
import java.util.Map;

public class Type {
    private static Map<String, Type> types = new HashMap<>();

    protected cum.jesus.cts.ctir.type.Type irType;

    public Type(cum.jesus.cts.ctir.type.Type irType) {
        this.irType = irType;
    }

    public cum.jesus.cts.ctir.type.Type getIRType() {
        return irType;
    }

    public boolean isIntegerType() {
        return false;
    }

    public boolean isStructType() {
        return false;
    }

    public boolean isArrayType() {
        return false;
    }

    public boolean isStringType() {
        return false;
    }

    public String getMangleID() {
        if (irType.isIntegerType()) {
            switch (((cum.jesus.cts.ctir.type.IntegerType) irType).getSizeInBits()) {
                case 64:
                    return "l";
                case 32:
                    return "i";
                case 16:
                    return "s";
                case 8:
                    return "b";
                case 1:
                    return "Z";
            }
        }

        if (irType.isPointerType()) {
            return new Type(irType.getPointerElementType()).getMangleID() + "&";
        }

        if (irType.isVoidType()) {
            return "V";
        }

        if (irType.isArrayType()) {
            return "[" + ((ArrayType) irType).getLength() + new Type(irType.getPointerElementType()).getMangleID();
        }

        if (irType.isStructType()) {
            String name = irType.getName();
            if (name.contains(".")) {
                name = name.substring(0, name.indexOf('.'));
            }
            return "S" + name.length() + name;
        }

        if (irType.isStringType()) {
            return "b&"; // 'b' is the mangle id for 8-bit integer and '&' if the suffix for a pointer or array reference and strings are array references to bytes
        }

        return "ERROR";
    }

    public Type getBase() {
        return this;
    }

    public static void init() {
        types.clear();

        types.put("byte", new IntegerType(8));
        types.put("short", new IntegerType(16));
        types.put("int", new IntegerType(32));
        types.put("long", new IntegerType(64));

        types.put("bool", new IntegerType(1));

        types.put("string", new StringType());
        types.put("void", new VoidType());
    }

    public static boolean exists(String name) {
        return types.containsKey(name);
    }

    public static Type get(String name) {
        return types.get(name);
    }

    public static void put(String name, Type type) {
        types.put(name, type);
    }

    public static Type getLong() {
        return types.get("long");
    }

    public static Type getInt() {
        return types.get("int");
    }

    public static Type getShort() {
        return types.get("short");
    }

    public static Type getByte() {
        return types.get("byte");
    }

    public static Type getString() {
        return types.get("string");
    }
}