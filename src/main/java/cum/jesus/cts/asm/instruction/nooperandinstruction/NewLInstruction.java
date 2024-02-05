package cum.jesus.cts.asm.instruction.nooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.NoOperandInstruction;

public final class NewLInstruction extends NoOperandInstruction {
    public NewLInstruction() {
        super(Opcodes.NEWL);
    }
}
