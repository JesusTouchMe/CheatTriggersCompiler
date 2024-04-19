package cum.jesus.cts.ctir.ir.constant;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class ConstantInt extends Constant {
    private final long value;

    public ConstantInt(Block parent, int id, long value, Type type) {
        super(parent, id);
        super.type = type;

        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean requiresRegister() {
        return color != -1;
    }

    @Override
    public List<Integer> getOperands() {
        return new ArrayList<>();
    }

    @Override
    public void print(PrintStream stream) {
        prints = false;
    }

    @Override
    public String ident() {
        return String.format("%s %d", type.getName(), value);
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (!register.isEmpty()) {
            values.add(new MovInstruction(Register.get(register), new Immediate(value)));
            emittedValue = Register.get(register);
        } else {
            emittedValue = new Immediate(value);
        }
    }
}
