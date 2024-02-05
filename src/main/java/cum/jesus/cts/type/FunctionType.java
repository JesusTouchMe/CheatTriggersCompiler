package cum.jesus.cts.type;

import java.util.List;

public final class FunctionType extends Type {
    private Type returnType;
    private List<Type> args;

    public FunctionType(Type returnType, final List<Type> args) {
        super(returnType.name + "()");
        this.returnType = returnType;
        this.args = args;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getArgs() {
        return args;
    }

    @Override
    public int getSize() {
        return returnType.getSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FunctionType)) {
            return false;
        }

        return ((FunctionType) obj).returnType.equals(returnType) && ((FunctionType) obj).args.equals(args);
    }

    public static FunctionType get(Type returnType, final List<Type> args) {
        return (FunctionType) Type.getFunctionType(returnType, args);
    }
}
