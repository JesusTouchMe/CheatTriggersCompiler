package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class LOrInstruction extends LogicalInstruction {
    public LOrInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.LOR, "lor", dest, left, right);
    }
}
