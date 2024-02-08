package cum.jesus.cts.asm.instruction.nooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.NoOperandInstruction;

import java.io.PrintStream;

public final class NopInstruction extends NoOperandInstruction {
    public NopInstruction() {
        super(Opcodes.NOP);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    nop");
    }
}
