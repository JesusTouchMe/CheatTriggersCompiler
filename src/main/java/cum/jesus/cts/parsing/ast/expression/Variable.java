package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.GlobalVariable;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.LocalSymbol;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class Variable extends AstNode {
    private String name;

    public String getName() {
        return name;
    }

    public Variable(List<String> annotations, final String name, Type type) {
        super(annotations);
        this.name = name;
        super.type = type;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Optional<Function> function = scope.findFunction(name);
        if (function.isPresent()) {
            return function.get();
        }

        Optional<LocalSymbol> variable = scope.findVariable(name);
        if (variable.isPresent()) {
            AtomicReference<Value> value = new AtomicReference<>();
            variable.get().alloca.consume(value::set, value::set);

            return builder.createLoad(value.get());
        }

        GlobalVariable global = module.getGlobal(name);
        if (global == null) {
            throw UnreachableStatementException.INSTANCE;
        }

        return builder.createLoad(global);
    }

    @Override
    public String toString(int indentationLevel) {
        return "(var \"" + type + "\" \"" + name + "\")";
    }

    @Override
    public String toString() {
        return name;
    }
}
