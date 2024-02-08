package cum.jesus.cts.asm.instruction;

public abstract class ThreeOperandInstruction extends Instruction {
    protected Operand first;
    protected Operand second;
    protected Operand third;

    protected ThreeOperandInstruction(Operand first, Operand second, Operand third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public Operand getFirst() {
        return first;
    }

    public Operand getSecond() {
        return second;
    }

    public Operand getThird() {
        return third;
    }
}
