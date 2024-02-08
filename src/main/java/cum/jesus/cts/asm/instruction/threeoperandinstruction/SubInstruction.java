package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class SubInstruction extends LogicalInstruction {
    public SubInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.SUB, "sub", dest, left, right);
    }
}
