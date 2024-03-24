package cum.jesus.cts.ctir.type;

import java.util.ArrayList;
import java.util.List;

public final class FunctionType extends Type {
    private Type returnType;
    private List<Type> arguments;

    FunctionType(Type returnType, List<Type> arguments) {
        super(0, returnType.name + "()");
        this.returnType = returnType;
        this.arguments = new ArrayList<>(arguments);
    }

    public static FunctionType get(Type returnType, List<Type> arguments) {
        return (FunctionType) Type.getFunctionType(returnType, arguments);
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getArguments() {
        return arguments;
    }

    public Type getArgument(int i) {
        return arguments.get(i);
    }
}
