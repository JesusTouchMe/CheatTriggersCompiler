package cum.jesus.cts.asm.instruction.operand;

public final class StackMemory extends Memory {
    public StackMemory(short address) {
        super(new Register(Register.regStackBase), address);
    }
}
