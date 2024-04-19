package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;

import java.util.List;

public final class StringLiteral extends AstNode {
    private String text;

    public StringLiteral(List<String> annotations, String text) {
        super(annotations);
        this.text = text;

        type = Type.getString();
    }

    public String getText() {
        return text;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        return builder.createConstantString(text, type.getIRType());
    }

    @Override
    public String toString(int indentationLevel) {
        return "(string \"" + text.replace("\n", "\\n") + "\")";
    }
}
