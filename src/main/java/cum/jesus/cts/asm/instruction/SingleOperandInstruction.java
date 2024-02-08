package cum.jesus.cts.asm.instruction;

import java.util.Objects;

public abstract class SingleOperandInstruction extends Instruction {
    protected Operand operand;

    protected SingleOperandInstruction(Operand operand) {
        this.operand = Objects.requireNonNull(operand);
    }

    public Operand getOperand() {
        return operand;
    }
}
