package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;

import java.io.PrintStream;

public final class LodInstruction extends TwoOperandInstruction {
    public LodInstruction(Operand dest, Operand source) {
        super(dest, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    lod %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {

    }
}
