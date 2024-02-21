package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class CmpLteInstruction extends LogicalInstruction {
    public CmpLteInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.CMPLTE, "cmplte", dest, left, right);
    }
}
