package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class Argument extends Value {
    private String name;

    Argument(Module module, int id, Type type, String name) {
        super(module, id);
        super.type = type;

        this.name = name;
    }

    @Override
    public boolean requiresRegister() {
        return color > -1;
    }

    @Override
    public List<Integer> getOperands() {
        return new ArrayList<>();
    }

    @Override
    public void print(PrintStream stream) {
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (color > -1) {
            emittedValue = Register.get(register);
        } else {
            emittedValue = new Memory(new Register(Register.regStackBase), (short) color);
        }
    }
}
