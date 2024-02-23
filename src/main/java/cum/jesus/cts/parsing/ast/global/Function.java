package cum.jesus.cts.parsing.ast.global;

import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.LocalSymbol;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.parsing.ast.statement.ReturnStatement;
import cum.jesus.cts.type.FunctionType;
import cum.jesus.cts.type.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Function extends AstNode {
    private Type returnType;
    private String name;
    private List<FunctionArgument> args;
    private List<AstNode> body;
    private Environment scope;

    /**
     * func add(int a, int b) = a + b; is a singleStatement function
     */
    private boolean singleStatement = false;

    public Function(Type type, final String name, List<FunctionArgument> args, List<AstNode> body, Environment scope) {
        this.returnType = type;
        super.type = type;

        this.name = name;
        this.args = args;
        this.body = body;
        this.scope = scope;
    }

    public Function singleStatement() {
        this.singleStatement = true;
        return this;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment __) {
        List<Type> argTypes = new ArrayList<>();
        for (FunctionArgument arg : args) {
            argTypes.add(arg.getType());
        }

        FunctionType functionType = FunctionType.get(returnType, argTypes);
        cum.jesus.cts.ctir.ir.Function function = cum.jesus.cts.ctir.ir.Function.create(functionType, module, name);
        Environment.functions.put(name, function);

        Block entry = Block.create("", function);
        builder.setInsertPoint(entry);

        if (!singleStatement) {
            int i = 0;
            for (FunctionArgument arg : args) {
                AllocaInst alloca = builder.createAlloca(arg.getType());
                scope.variables.put(arg.getName(), new LocalSymbol(alloca, arg.getType()));
                builder.createStore(alloca, function.getArgument(i++));
            }
        } else {
            int i = 0;
            for (FunctionArgument arg : args) {
                scope.variables.put(arg.getName(), new LocalSymbol(function.getArgument(i++), arg.getType()));
            }
        }

        for (AstNode node : body) {
            node.emit(module, builder, scope);
        }

        if (!(body.get(body.size() - 1) instanceof ReturnStatement)) {
            builder.createRet(null);
        }

        return function;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("func<")
                .append(returnType.getName())
                .append("> ")
                .append(name)
                .append('(');

        Iterator<FunctionArgument> it = args.iterator();
        while (it.hasNext()) {
            FunctionArgument arg = it.next();
            sb.append(arg.toString());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") {\n").append("    ");

        for (AstNode node : body) {
            sb.append(node.toString()).append("\n    ");
        }
        sb.append('}');

        return sb.toString();
    }
}
