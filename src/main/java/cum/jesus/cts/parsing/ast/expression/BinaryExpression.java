package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.Instruction;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.lexing.TokenType;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

public final class BinaryExpression extends AstNode {
    private AstNode left;
    private Operator operator;
    private AstNode right;

    public BinaryExpression(AstNode left, TokenType operator, AstNode right) {
        this.left = left;
        this.right = right;

        switch (operator) {
            case PLUS:
                this.operator = Operator.ADD;
                break;
            case MINUS:
                this.operator = Operator.SUB;
                break;
            case STAR:
                this.operator = Operator.MUL;
                break;
            case SLASH:
                this.operator = Operator.DIV;
                break;

            case EQUALS:
                this.operator = Operator.ASSIGN;
                break;

            case DOUBLE_EQUALS:
                this.operator = Operator.EQUAL;
                break;
            case BANG_EQUALS:
                this.operator = Operator.NOT_EQUAL;
                break;
            case LEFT_ANGLE_BRACKET:
                this.operator = Operator.LESS;
                break;
            case RIGHT_ANGLE_BRACKET:
                this.operator = Operator.GREATER;
                break;
            case LEFT_ANGLE_BRACKET_EQUALS:
                this.operator = Operator.LESS_EQUAL;
                break;
            case RIGHT_ANGLE_BRACKET_EQUALS:
                this.operator = Operator.GREATER_EQUAL;
                break;

            default:
                throw new RuntimeException("Unknown binary operator");
        }

        type = left.getType();
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Value leftValue = left.emit(module, builder, scope);
        Value rightValue = right.emit(module, builder, scope);

        switch (operator) {
            case ADD:
                return builder.createAdd(leftValue, rightValue);
            case SUB:
                return builder.createSub(leftValue, rightValue);
            case MUL:
                return builder.createMul(leftValue, rightValue);
            case DIV:
                return builder.createDiv(leftValue, rightValue);

            case ASSIGN:
                Instruction instruction = (Instruction) leftValue;
                instruction.eraseFromParent();
                return builder.createStore(Module.getPointerOperand(leftValue), rightValue);

            case EQUAL:
                return builder.createCmpEq(leftValue, rightValue);
            case NOT_EQUAL:
                return builder.createCmpNe(leftValue, rightValue);
            case LESS:
                return builder.createCmpLt(leftValue, rightValue);
            case GREATER:
                return builder.createCmpGt(leftValue, rightValue);
            case LESS_EQUAL:
                return builder.createCmpLte(leftValue, rightValue);
            case GREATER_EQUAL:
                return builder.createCmpGte(leftValue, rightValue);
        }

        throw UnreachableStatementException.INSTANCE;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(operator.toString()).append(" \"").append(type.toString()).append("\"\n");

        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(left.toString(indentationLevel + 1)).append('\n');

        for (int i = 0; i < indentationLevel + 1; i++) {
            sb.append("  ");
        }

        sb.append(right.toString(indentationLevel + 1)).append(')');
        return sb.toString();
    }

    public enum Operator {
        ADD("add"), SUB("sub"),
        MUL("mul"), DIV("div"),
        ASSIGN("assign"),
        EQUAL("eq"), NOT_EQUAL("ne"),
        LESS("lt"), GREATER("gt"),
        LESS_EQUAL("lte"), GREATER_EQUAL("gte"),

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
