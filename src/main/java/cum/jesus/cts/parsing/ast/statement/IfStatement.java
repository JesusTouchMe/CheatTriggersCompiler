package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.List;

public final class IfStatement extends AstNode {
    private AstNode condition;
    private AstNode body;
    private AstNode elseBody;

    public IfStatement(List<String> annotations, AstNode condition, AstNode body, AstNode elseBody) {
        super(annotations);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Value condition = this.condition.emit(module, builder, scope);
        Block trueBlock = Block.create("", builder.getInsertPoint().getParent());
        Block elseBlock = null;

        if (elseBody != null) {
            elseBlock = Block.create("", builder.getInsertPoint().getParent());
        }

        Block mergeBlock = Block.create("", builder.getInsertPoint().getParent());

        if (elseBody != null) {
            builder.createCondBr(condition, trueBlock, elseBlock);
        } else {
            builder.createCondBr(condition, trueBlock, mergeBlock);
        }

        builder.setInsertPoint(trueBlock);
        body.emit(module, builder, scope);
        builder.createBr(mergeBlock);

        if (elseBody != null) {
            builder.setInsertPoint(elseBlock);
            elseBody.emit(module, builder, scope);
            builder.createBr(mergeBlock);
        }

        builder.setInsertPoint(mergeBlock);

        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(if\n");

        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(condition.toString(indentationLevel + 1)).append('\n');
        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(body.toString(indentationLevel + 1));

        if (elseBody != null) {
            sb.append('\n');

            for (int i = 0; i < indentationLevel + 1; i++) {
                sb.append("  ");
            }

            sb.append(elseBody.toString(indentationLevel + 1));
        }

        sb.append(')');

        return sb.toString();
    }
}
