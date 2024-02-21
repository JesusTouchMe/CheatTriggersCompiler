package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class CmpGtInstruction extends LogicalInstruction {
    public CmpGtInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.CMPGT, "cmpgt", dest, left, right);
    }
}
