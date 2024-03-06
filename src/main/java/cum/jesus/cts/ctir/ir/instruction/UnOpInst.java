package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.NegInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class UnOpInst extends Instruction {
    private int operand;
    private Operator op;
    private String name;

    public UnOpInst(Block parent, int id, Value operand, Operator op, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.operand = operand.getId();
        this.op = op;
        this.name = name;

        switch (op) {
            case POS:
            case NEG:
                super.type = operand.getType();
                break;
        }
    }

    @Override
    public boolean requiresRegister() {
        return color != -1;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(operand);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = %s %s", name, op.str, parent.getParent().getValue(operand).ident());
    }

    @Override
    public String ident() {
        return "%" + name;
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand operand = parent.getEmittedValue(this.operand);

        switch (op) {
            case POS: // genuinely who the fuck needs this to be the real instruction it just does pointless arithmetic on the vm level and moves values. mov is WAY more efficient at that
                if (color != -1) {
                    values.add(new MovInstruction(Register.get(register), operand));
                    emittedValue = Register.get(register);
                } else {
                    emittedValue = operand;
                }
                break;

            case NEG:
                if (color != -1) {
                    values.add(new NegInstruction(Register.get(register), operand));
                    emittedValue = Register.get(register);
                } else {
                    values.add(new NegInstruction(operand.clone(), operand.clone()));
                    emittedValue = operand;
                }
        }
    }

    public enum Operator {
        POS("pos"), NEG("neg");

        private String str;

        Operator(String str) {
            this.str = str;
        }
    }
}
