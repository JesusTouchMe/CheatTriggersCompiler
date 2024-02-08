package cum.jesus.cts.asm.instruction.nooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.instruction.NoOperandInstruction;

import java.io.PrintStream;

public final class RetInstruction extends NoOperandInstruction {
    public RetInstruction() {
        super(Opcodes.RET);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    ret");
    }
}
