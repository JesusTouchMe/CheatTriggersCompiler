package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.Operand;

public final class DivInstruction extends LogicalInstruction {
    public DivInstruction(Operand dest, Operand left, Operand right) {
        super(Opcodes.DIV, "div", dest, left, right);
    }
}
