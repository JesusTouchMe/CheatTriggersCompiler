package cum.jesus.cts.asm.instruction;

public abstract class SingleOperandInstruction extends Instruction {
    protected Operand operand;

    protected SingleOperandInstruction(Operand operand) {
        this.operand = operand;
    }

    public Operand getOperand() {
        return operand;
    }
}
