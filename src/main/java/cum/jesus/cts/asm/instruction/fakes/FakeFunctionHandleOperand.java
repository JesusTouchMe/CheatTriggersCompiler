package cum.jesus.cts.asm.instruction.fakes;

import cum.jesus.cts.asm.instruction.Operand;

public class FakeFunctionHandleOperand extends Operand {
    private String name;

    public FakeFunctionHandleOperand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String ident() {
        return '"' + name + '"';
    }

    @Override
    public Operand clone() {
        return new FakeFunctionHandleOperand(name);
    }
}
