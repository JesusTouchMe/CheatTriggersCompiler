package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;

import java.util.List;

public final class IntegerLiteral extends AstNode {
    private long value;

    public IntegerLiteral(List<String> annotations, long value) {
        super(annotations);
        this.value = value;

        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            type = Type.getLong();
        } else {
            type = Type.getInt();
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        return builder.createConstantInt(value, type.getIRType());
    }

    @Override
    public String toString(int indentationLevel) {
        return "(int \"" + type.toString() + "\" " + value + ")";
    }
}
