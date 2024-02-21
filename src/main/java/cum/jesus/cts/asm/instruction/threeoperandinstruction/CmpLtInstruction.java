package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class CmpLtInstruction extends LogicalInstruction {
    public CmpLtInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.CMPLT, "cmplt", dest, left, right);
    }
}
