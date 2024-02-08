package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class MulInstruction extends LogicalInstruction {
    public MulInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.MUL, "mul", dest, left, right);
    }
}
