package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class OrInstruction extends LogicalInstruction {
    public OrInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.OR, "or", dest, left, right);
    }
}
