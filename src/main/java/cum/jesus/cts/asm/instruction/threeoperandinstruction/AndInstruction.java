package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class AndInstruction extends LogicalInstruction {
    public AndInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.AND, "and", dest, left, right);
    }
}
