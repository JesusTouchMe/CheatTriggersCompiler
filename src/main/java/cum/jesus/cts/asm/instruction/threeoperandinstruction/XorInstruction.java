package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class XorInstruction extends LogicalInstruction {
    public XorInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.XOR, "xor", dest, left, right);
    }
}
