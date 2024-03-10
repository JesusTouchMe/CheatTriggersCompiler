package cum.jesus.cts.type;

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