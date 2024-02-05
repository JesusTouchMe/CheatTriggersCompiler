package cum.jesus.cts.asm.instruction;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;

public abstract class AsmValue {
    public abstract void emit(OpcodeBuilder builder);
}
