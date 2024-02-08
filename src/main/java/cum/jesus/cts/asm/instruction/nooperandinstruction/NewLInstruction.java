package cum.jesus.cts.asm.instruction.nooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.NoOperandInstruction;

import java.io.PrintStream;

public final class NewLInstruction extends NoOperandInstruction {
    public NewLInstruction() {
        super(Opcodes.NEWL);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    newl");
    }
}
