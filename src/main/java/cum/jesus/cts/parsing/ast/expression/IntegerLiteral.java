package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;

public final class IntegerLiteral extends AstNode {
    private long value;

    public IntegerLiteral(long value) {
        this.value = value;

        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            type = Type.getIntegerType(64);
        } else {
            type = Type.getIntegerType(32);
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        return builder.createConstantInt(value, type);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
