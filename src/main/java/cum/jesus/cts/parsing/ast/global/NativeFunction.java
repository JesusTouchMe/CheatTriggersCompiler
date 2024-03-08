package cum.jesus.cts.parsing.ast.global;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.FunctionType;
import cum.jesus.cts.type.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NativeFunction extends AstNode {
    private Type returnType;
    private String name;
    private List<FunctionArgument> args;

    public NativeFunction(Type returnType, String name, List<FunctionArgument> args) {
        this.returnType = returnType;
        super.type = returnType;

        this.name = name;
        this.args = args;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        List<Type> argTypes = new ArrayList<>();
        for (FunctionArgument arg : args) {
            argTypes.add(arg.getType());
        }

        FunctionType functionType = FunctionType.get(returnType, argTypes);
        Function function = Function.create(functionType, module, name);

        Environment.functions.put(name, function);

        return function;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < indentationLevel; i++) {
            sb.append("  ");
        }

        sb.append("\n\n(native \"").append(type.toString()).append("\" ");
        sb.append("\"").append(name).append("\" ");

        Iterator<FunctionArgument> fit = args.iterator();
        while (fit.hasNext()) {
            FunctionArgument arg = fit.next();

            for (int i = 0; i < indentationLevel + 1; i++) {
                sb.append("  ");
            }

            sb.append(arg.toString());

            if (fit.hasNext()) {
                sb.append("\n");
            }
        }

        sb.append(')');
        return sb.toString();
    }
}
