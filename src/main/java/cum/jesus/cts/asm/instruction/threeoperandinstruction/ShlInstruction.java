package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class ShlInstruction extends LogicalInstruction {
    public ShlInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.SHL, "shl", dest, left, right);
    }
}
