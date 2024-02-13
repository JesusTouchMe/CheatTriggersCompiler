package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.lexing.TokenType;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

public final class UnaryExpression extends AstNode {
    private AstNode operand;
    private Operator operator;

    public UnaryExpression(AstNode operand, TokenType operator) {
        this.operand = operand;
        switch (operator) {
            case PLUS:
                this.operator = Operator.POSITIZE;
                break;
            case MINUS:
                this.operator = Operator.NEGATE;
                break;

            default:
                // TODO: error here
                break;
        }

        type = operand.getType();
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Value operandValue = operand.emit(module, builder, scope);

        switch (operator) {
            case POSITIZE:
                return builder.createPos(operandValue);
            case NEGATE:
                return builder.createNeg(operandValue);
        }

        throw UnreachableStatementException.INSTANCE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append('(');
        if (!operator.isAfter()) {
            sb.append(operator.toString());
        }
        sb.append(operand.toString());
        if (operator.isAfter()) {
            sb.append(operator.toString());
        }
        sb.append(')');
        return sb.toString();
    }

    public enum Operator {
        POSITIZE("+", false),
        NEGATE("-", false);

        private String str;
        // true means put string before, false means put string after
        private boolean after;
        Operator(String str, boolean after) {
            this.str = str;
            this.after = after;
        }

        @Override
        public String toString() {
            return str;
        }

        public boolean isAfter() {
            return after;
        }
    }
}
