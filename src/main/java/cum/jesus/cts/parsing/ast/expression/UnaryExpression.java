package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.lexing.TokenType;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

import java.util.List;

public final class UnaryExpression extends AstNode {
    private AstNode operand;
    private Operator operator;

    public UnaryExpression(List<String> annotations, AstNode operand, TokenType operator) {
        super(annotations);
        this.operand = operand;

        switch (operator) {
            case PLUS:
                this.operator = Operator.POSITIZE;
                break;
            case MINUS:
                this.operator = Operator.NEGATE;
                break;
            case KEYWORD_NEW:
                this.operator = Operator.NEW;
                break;
            case KEYWORD_DELETE:
                this.operator = Operator.DELETE;
                break;

            default:
                throw new RuntimeException("Unknown operator");
        }

        switch (this.operator) {
            case POSITIZE:
            case NEGATE:
                type = operand.getType();
                break;

            case NEW:
                type = Type.get(((Variable) operand).getName());
                break;

            case DELETE:
                type = Type.get("void");
                break;
        }
    }

    public AstNode getOperand() {
        return operand;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Value operandValue = operand.emit(module, builder, scope);

        switch (operator) {
            case POSITIZE:
                return builder.createPos(operandValue);
            case NEGATE:
                return builder.createNeg(operandValue);
            case NEW:
                return operandValue;
            case DELETE:
                return builder.createFree(operandValue);
        }

        throw UnreachableStatementException.INSTANCE;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(operator.toString()).append(" \"").append(type.toString()).append("\"\n");

        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(operand.toString(indentationLevel + 1));
        sb.append(')');

        return sb.toString();
    }

    public enum Operator {
        POSITIZE("pos"),
        NEGATE("neg"),

        NEW("new"),
        DELETE("del"),

        ;

        private String str;
        Operator(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }
}
