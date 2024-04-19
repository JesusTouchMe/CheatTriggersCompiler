package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.List;

public final class WhileStatement extends AstNode {
    private AstNode condition;
    private AstNode body;

    public WhileStatement(List<String> annotations, AstNode condition, AstNode body) {
        super(annotations);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Block conditionBlock = Block.create("", builder.getInsertPoint().getParent());
        Block bodyBlock = Block.create("", builder.getInsertPoint().getParent());
        Block mergeBlock = Block.create("", builder.getInsertPoint().getParent());

        builder.createBr(conditionBlock);
        builder.setInsertPoint(conditionBlock);

        Value condition = this.condition.emit(module, builder, scope);

        builder.createCondBr(condition, bodyBlock, mergeBlock);

        builder.setInsertPoint(bodyBlock);
        body.emit(module, builder, scope);
        builder.createBr(conditionBlock);

        builder.setInsertPoint(mergeBlock);

        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(while\n");

        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(condition.toString(indentationLevel + 1)).append('\n');
        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(body.toString(indentationLevel + 1));
        sb.append(')');

        return sb.toString();
    }
}
