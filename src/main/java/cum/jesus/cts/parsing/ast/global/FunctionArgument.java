package cum.jesus.cts.parsing.ast.global;

import cum.jesus.cts.type.Type;

public final class FunctionArgument {
    private Type type;
    private String name;

    public FunctionArgument(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return type.getName() + ' ' + name;
    }
}
