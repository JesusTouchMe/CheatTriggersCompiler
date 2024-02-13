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

            default:
                // TODO: error here
                break;
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
        }

        throw UnreachableStatementException.INSTANCE;
    }

    @Override
    public String toString() {
        return '(' + left.toString() + ' ' + operator.toString() + ' ' + right.toString() + ')';
    }

    public enum Operator {
        ADD("+"), SUB("-"),
        MUL("*"), DIV("/"),
        ASSIGN("=");

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
