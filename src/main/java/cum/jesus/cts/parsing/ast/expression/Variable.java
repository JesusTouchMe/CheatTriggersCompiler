package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.LocalSymbol;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

import java.util.Optional;

public final class Variable extends AstNode {
    private String name;

    public String getName() {
        return name;
    }

    public Variable(final String name, Type type) {
        this.name = name;
        super.type = type;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Function function = Environment.functions.get(name);
        if (function != null) {
            return function;
        }

        Optional<LocalSymbol> variable = scope.findVariable(name);
        if (!variable.isPresent()) {
            throw UnreachableStatementException.INSTANCE;
        }

        return builder.createLoad(variable.get().alloca);
    }

    @Override
    public String toString() {
        return name;
    }
}
