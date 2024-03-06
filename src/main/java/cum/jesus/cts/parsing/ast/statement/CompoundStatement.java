package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.Iterator;
import java.util.List;

public final class CompoundStatement extends AstNode {
    private List<AstNode> body;
    private Environment scope;

    public CompoundStatement(List<AstNode> body, Environment scope) {
        this.body = body;
        this.scope = scope;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment __) {
        builder.saveStackOffset();

        for (AstNode node : body) {
            node.emit(module, builder, scope);
        }

        builder.setStackOffset();

        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(list\n");

        Iterator<AstNode> it = body.iterator();
        while (it.hasNext()) {
            AstNode node = it.next();

            for (int i = 0; i < indentationLevel + 1; i++) {
                sb.append("  ");
            }

            sb.append(node.toString(indentationLevel + 1));

            if (it.hasNext()) {
                sb.append("\n");
            }
        }

        sb.append(')');

        return sb.toString();
    }
}
