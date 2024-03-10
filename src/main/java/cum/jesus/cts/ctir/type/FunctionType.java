package cum.jesus.cts.ctir.type;

import java.util.ArrayList;
import java.util.List;

public final class FunctionType extends Type {
    private Type returnType;
    private List<Type> arguments;

    FunctionType(Type returnType, List<Type> arguments) {
        super(0, returnType.name + "()");
        this.returnType = returnType;
        this.arguments = new ArrayList<>();

        for (Type arg : arguments) {
            if (arg instanceof StructType) {
                this.arguments.addAll(((StructType) arg).getFieldTypes());
            } else {
                this.arguments.add(arg);
            }
        }
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
}
