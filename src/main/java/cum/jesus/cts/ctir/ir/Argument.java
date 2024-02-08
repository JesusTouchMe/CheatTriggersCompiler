package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.type.Type;

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
        return true;
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
        emittedValue = Register.get(register);
    }
}
