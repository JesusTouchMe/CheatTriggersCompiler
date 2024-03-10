package cum.jesus.cts.ctir.ir.constant;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class ConstantString extends Constant {
    private String value;
    private String name;

    public ConstantString(Block parent, int id, String value, Type type, String name) {
        super(parent.getParent().getModule(), id);

        this.value = value;
        this.name = name;

        super.type = type;
    }

    public String getValue() {
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
        stream.printf("%%%s = %s %s", name, type.getName(), value);
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (!module.hasString(value)) {
            values.add(new ConstantPoolFake(new StringOperand(value)));
            module.insertString(value);
        }

        if (!register.isEmpty()) {
            values.add(new MovInstruction(Register.get(register), new ConstPoolEntryOperand(module.getString(value))));
            emittedValue = Register.get(register);
        } else {
            emittedValue = new ConstPoolEntryOperand(module.getString(value));
        }
    }
}
