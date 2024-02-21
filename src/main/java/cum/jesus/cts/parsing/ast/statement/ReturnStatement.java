package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

public final class ReturnStatement extends AstNode {
    private AstNode value;

    public ReturnStatement(AstNode value) {
        this.value = value;
        type = value.getType();
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        if (value != null) {
            Value returnValue = value.emit(module, builder, scope);
            return builder.createRet(returnValue);
        }

        return builder.createRet(null);
    }

    @Override
    public String toString() {
        if (value == null) {
            return "return";
        }
        return "return " + value;
    }
}