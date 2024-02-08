package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class LXorInstruction extends LogicalInstruction {
    public LXorInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.LXOR, "lxor", dest, left, right);
    }
}
