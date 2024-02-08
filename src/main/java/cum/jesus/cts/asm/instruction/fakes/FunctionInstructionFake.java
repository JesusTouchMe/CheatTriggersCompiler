package cum.jesus.cts.asm.instruction.fakes;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;

import java.io.PrintStream;

/**
 * Fake instruction which doesn't write any bytecode and only starts a new function declaration
 */
public final class FunctionInstructionFake extends AsmValue {
    private final String name;

    public FunctionInstructionFake(String name) {
        this.name = name;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("\n%s():\n", name);
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        builder.beginNewFunction(name);
    }
}
