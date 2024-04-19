package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.List;

public final class ForStatement extends AstNode {
    private AstNode initExpr;
    private AstNode condition;
    private AstNode loopExpr;
    private AstNode body;
    private Environment scope;

    public ForStatement(List<String> annotations, AstNode initExpr, AstNode condition, AstNode loopExpr, AstNode body, Environment scope) {
        super(annotations);
        this.initExpr = initExpr;
        this.condition = condition;
        this.loopExpr = loopExpr;
        this.body = body;
        this.scope = scope;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment __) {
        if (initExpr != null) {
            initExpr.emit(module, builder, scope);
        }

        Block conditionBlock = Block.create("", builder.getInsertPoint().getParent());
        Block bodyBlock = Block.create("", builder.getInsertPoint().getParent());
        Block mergeBlock = Block.create("", builder.getInsertPoint().getParent());

        builder.createBr(conditionBlock);
        builder.setInsertPoint(conditionBlock);

        if (condition != null) {
            builder.createCondBr(condition.emit(module, builder, scope), bodyBlock, mergeBlock);
        } else {
            builder.createBr(bodyBlock);
        }

        builder.setInsertPoint(bodyBlock);

        body.emit(module, builder, scope);

        if (loopExpr != null) {
            loopExpr.emit(module, builder, scope);
        }

        builder.createBr(conditionBlock);

        builder.setInsertPoint(mergeBlock);

        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        return null;
    }
}
