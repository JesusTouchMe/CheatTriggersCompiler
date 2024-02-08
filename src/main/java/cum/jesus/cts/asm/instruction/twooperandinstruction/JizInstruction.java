package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;

import java.io.PrintStream;

public final class JizInstruction extends TwoOperandInstruction {
    public JizInstruction(Operand source, Operand jumpCount) {
        super(source, jumpCount);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    jiz %s, %s", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {

    }
}
