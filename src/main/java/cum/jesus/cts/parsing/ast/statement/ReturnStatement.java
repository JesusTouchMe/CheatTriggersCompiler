package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.List;

public final class ReturnStatement extends AstNode {
    private AstNode value;

    public ReturnStatement(List<String> annotations, AstNode value) {
        super(annotations);
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
    public String toString(int indentationLevel) {
        if (value == null) {
            return "(return \"void\")";
        }
        return "(return \"" + type.toString() + "\" " + value.toString(indentationLevel) + ')';
    }
}
