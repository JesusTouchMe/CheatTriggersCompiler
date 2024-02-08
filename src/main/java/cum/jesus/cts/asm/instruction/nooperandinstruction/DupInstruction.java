package cum.jesus.cts.asm.instruction.nooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.NoOperandInstruction;

import java.io.PrintStream;

public final class DupInstruction extends NoOperandInstruction {
    public DupInstruction() {
        super(Opcodes.DUP);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    dup");
    }
}
