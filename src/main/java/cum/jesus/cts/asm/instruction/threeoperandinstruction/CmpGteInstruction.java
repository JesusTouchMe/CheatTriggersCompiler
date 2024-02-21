package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class CmpGteInstruction extends LogicalInstruction {
    public CmpGteInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.CMPGTE, "cmpgte", dest, left, right);
    }
}
