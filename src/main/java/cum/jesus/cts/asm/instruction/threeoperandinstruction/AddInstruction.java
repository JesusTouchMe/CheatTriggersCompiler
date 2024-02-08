package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class AddInstruction extends LogicalInstruction {
    public AddInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.ADD, "add", dest, left, right);
    }
}
