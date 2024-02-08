package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class ShrInstruction extends LogicalInstruction {
    public ShrInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.SHR, "shr", dest, left, right);
    }
}
