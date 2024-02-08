package cum.jesus.cts.asm.instruction;

public abstract class TwoOperandInstruction extends Instruction {
    protected Operand left;
    protected Operand right;

    protected TwoOperandInstruction(Operand left, Operand right) {
        this.left = left;
        this.right = right;
    }

    public Operand getLeft() {
        return left;
    }

    public Operand getRight() {
        return right;
    }
}
