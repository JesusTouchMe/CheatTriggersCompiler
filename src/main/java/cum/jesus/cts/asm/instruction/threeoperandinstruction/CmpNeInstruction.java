package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class CmpNeInstruction extends LogicalInstruction {
    public CmpNeInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.CMPNE, "cmpne", dest, left, right);
    }
}
