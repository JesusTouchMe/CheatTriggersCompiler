package cum.jesus.cts.ctir.ir.constant;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class ConstantInt extends Constant {
    private final long value;
    private final String name;

    public ConstantInt(Block parent, int id, long value, Type type, String name) {
        super(parent.getParent().getModule(), id);

        this.value = value;
        this.name = name;

        super.type = type;
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
        stream.printf("%%%s = %s %d", name, type.getName(), value);
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
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
