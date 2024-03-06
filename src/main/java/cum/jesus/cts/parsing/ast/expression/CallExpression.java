package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CallExpression extends AstNode {
    private AstNode callee;
    private List<AstNode> params;

    public CallExpression(AstNode callee, List<AstNode> params) {
        this.callee = callee;
        this.params = params;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Value calleeValue = callee.emit(module, builder, scope);

        List<Value> parameters = new ArrayList<>(params.size());
        for (AstNode param : params) {
            parameters.add(param.emit(module, builder, scope));
        }

        return builder.createCall(calleeValue, parameters);
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(call \"").append(callee.toString()).append("\" (list ");

        Iterator<AstNode> it = params.iterator();
        while (it.hasNext()) {
            AstNode param = it.next();

            sb.append(param.toString(indentationLevel + 1));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append("))");
        return sb.toString();
    }
}
