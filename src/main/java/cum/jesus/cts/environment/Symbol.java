package cum.jesus.cts.environment;

import cum.jesus.cts.type.Type;

public final class Symbol {
    private Type type;
    private String name;

    public Symbol(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
