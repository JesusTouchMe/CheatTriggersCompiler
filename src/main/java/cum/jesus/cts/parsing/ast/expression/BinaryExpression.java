package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.Instruction;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.lexing.TokenType;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.StructType;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.Pair;

import java.util.List;

public final class BinaryExpression extends AstNode {
    private AstNode left;
    private Operator operator;
    private AstNode right;

    public BinaryExpression(List<String> annotations, AstNode left, TokenType operator, AstNode right) {
        super(annotations);
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

            case DOT:
                this.operator = Operator.MEMBER_ACCESS;
                break;
            case LEFT_BRACKET:
                this.operator = Operator.SUBSCRIPT;
                break;

            default:
                throw new RuntimeException("Unknown binary operator");
        }

        if (this.operator == Operator.ASSIGN) {
            type = left.getType();
        } else if (this.operator == Operator.SUBSCRIPT) {
            type = left.getType().getBase();
        } else if (this.operator == Operator.MEMBER_ACCESS) {
            if (left.getType() == null || !left.getType().isStructType());
            else {
                type = new Type(((StructType) left.getType()).getMemberIndex(((Variable) right).getName()).second);
            }
        } else if (this.operator == Operator.EQUAL || this.operator == Operator.NOT_EQUAL
        || this.operator == Operator.LESS || this.operator == Operator.GREATER
        || this.operator == Operator.LESS_EQUAL || this.operator == Operator.GREATER_EQUAL) {
            type = Type.get("bool");
        } else {
            type = left.getType();
        }
    }

    public AstNode getLeft() {
        return left;
    }

    public Operator getOperator() {
        return operator;
    }

    public AstNode getRight() {
        return right;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        Value lhs;

        if (operator == Operator.MEMBER_ACCESS) {
            if (left instanceof Variable && Environment.scopes.containsKey(((Variable) left).getName())) {
                Environment leftScope = Environment.scopes.get(((Variable) left).getName());

                return right.emit(Environment.modules.get(((Variable) left).getName()), builder, leftScope);
            } else {
                lhs = left.emit(module, builder, scope);

                Pair<Integer, cum.jesus.cts.ctir.type.Type> field = ((StructType) left.getType()).getMemberIndex(((Variable) right).getName());
                Instruction inst = (Instruction) lhs;
                Value ptr = Module.getPointerOperand(inst);

                Value gep = builder.createStructGEP(ptr.getType().getPointerElementType(), ptr, field.first);

                Value load = builder.createLoad(gep);
                //load.setType(gep.getType());

                inst.eraseFromParent();

                return load;
            }
        }

        if (operator == Operator.SUBSCRIPT && left instanceof UnaryExpression && ((UnaryExpression) left).getOperand() instanceof Variable && Type.exists(((Variable) ((UnaryExpression) left).getOperand()).getName())) {
            Type elementType = Type.get(((Variable) ((UnaryExpression) left).getOperand()).getName());

            Value count = right.emit(module, builder, scope);
            return builder.createMalloc(elementType.getIRType(), count);
        }

        lhs = left.emit(module, builder, scope);
        Value rhs = right.emit(module, builder, scope);

        switch (operator) {
            case ADD:
                return builder.createAdd(lhs, rhs);
            case SUB:
                return builder.createSub(lhs, rhs);
            case MUL:
                return builder.createMul(lhs, rhs);
            case DIV:
                return builder.createDiv(lhs, rhs);

            case ASSIGN:
                Instruction instruction = (Instruction) lhs;
                instruction.eraseFromParent();
                return builder.createStore(Module.getPointerOperand(lhs), rhs);

            case EQUAL:
                return builder.createCmpEq(lhs, rhs);
            case NOT_EQUAL:
                return builder.createCmpNe(lhs, rhs);
            case LESS:
                return builder.createCmpLt(lhs, rhs);
            case GREATER:
                return builder.createCmpGt(lhs, rhs);
            case LESS_EQUAL:
                return builder.createCmpLte(lhs, rhs);
            case GREATER_EQUAL:
                return builder.createCmpGte(lhs, rhs);

            case SUBSCRIPT: {
                Instruction inst = (Instruction) lhs;
                Value ptr = Module.getPointerOperand(inst);

                Value gep = builder.createGEP(lhs.getType(), ptr, rhs);
                Value load = builder.createLoad(gep);

                inst.eraseFromParent();

                return load;
            }
        }

        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(operator.toString()).append(" \"").append(type).append("\"\n");

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

        MEMBER_ACCESS("member"),
        SUBSCRIPT("subscript"),

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
