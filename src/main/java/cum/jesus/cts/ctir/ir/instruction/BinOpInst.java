package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.AddInstruction;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.DivInstruction;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.MulInstruction;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.SubInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public final class BinOpInst extends Instruction {
    private int left;
    private Operator op;
    private int right;
    private String name;

    public BinOpInst(Block parent, int id, Value left, Operator op, Value right, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.left = left.getId();
        this.op = op;
        this.right = right.getId();
        this.name = name;

        assert left.getType().equals(right.getType());

        switch (op) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
                super.type = left.getType();
                break;
        }
    }

    @Override
    public boolean requiresRegister() {
        return true; // dest
    }

    @Override
    public List<Integer> getOperands() {
        return Arrays.asList(left, right);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = %s %s, %s", name, op.str, parent.getParent().getValue(left).ident(), parent.getParent().getValue(right).ident());
    }

    @Override
    public String ident() {
        return "%" + name;
    }

    @Override
    public void emit(List<AsmValue> values) {
        switch (op) {
            case ADD: {
                Operand lhs = parent.getEmittedValue(left);
                Operand rhs = parent.getEmittedValue(right);

                if (color != -1) {
                    values.add(new AddInstruction(Register.get(register), lhs, rhs));
                    emittedValue = Register.get(register);
                } else {
                    values.add(new AddInstruction(lhs.clone(), lhs.clone(), rhs));
                    emittedValue = lhs;
                }
            } break;

            case SUB: {
                Operand lhs = parent.getEmittedValue(left);
                Operand rhs = parent.getEmittedValue(right);

                if (color != -1) {
                    values.add(new SubInstruction(Register.get(register), lhs, rhs));
                    emittedValue = Register.get(register);
                } else {
                    values.add(new SubInstruction(lhs.clone(), lhs.clone(), rhs));
                    emittedValue = lhs;
                }
            } break;

            case MUL: {
                Operand lhs = parent.getEmittedValue(left);
                Operand rhs = parent.getEmittedValue(right);

                if (color != -1) {
                    values.add(new MulInstruction(Register.get(register), lhs, rhs));
                    emittedValue = Register.get(register);
                } else {
                    values.add(new MulInstruction(lhs.clone(), lhs.clone(), rhs));
                    emittedValue = lhs;
                }
            } break;

            case DIV: {
                Operand lhs = parent.getEmittedValue(left);
                Operand rhs = parent.getEmittedValue(right);

                if (color != -1) {
                    values.add(new DivInstruction(Register.get(register), lhs, rhs));
                    emittedValue = Register.get(register);
                } else {
                    values.add(new DivInstruction(lhs.clone(), lhs.clone(), rhs));
                    emittedValue = lhs;
                }
            } break;
        }
    }

    public enum Operator {
        ADD("add"), SUB("sub"),
        MUL("mul"), DIV("div");

        private final String str;

        Operator(String str) {
            this.str = str;
        }
    }
}
