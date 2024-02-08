package cum.jesus.cts.asm.instruction;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;

import java.io.PrintStream;

public abstract class AsmValue {
    public abstract void print(PrintStream stream);

    public abstract void emit(OpcodeBuilder builder);
}
