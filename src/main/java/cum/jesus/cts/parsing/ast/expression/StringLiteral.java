package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

public final class StringLiteral extends AstNode {
    private String text;

    public StringLiteral(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        return null;
    }

    @Override
    public String toString() {
        return '"' + text + '"';
    }
}
