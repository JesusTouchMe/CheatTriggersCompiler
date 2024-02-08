package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class LAndInstruction extends LogicalInstruction {
    public LAndInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.LAND, "land", dest, left, right);
    }
}
