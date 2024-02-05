package cum.jesus.cts.asm.instruction.nooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.NoOperandInstruction;

public final class RetInstruction extends NoOperandInstruction {
    public RetInstruction() {
        super(Opcodes.RET);
    }
}
