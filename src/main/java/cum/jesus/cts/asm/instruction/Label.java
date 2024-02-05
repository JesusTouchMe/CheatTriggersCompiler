package cum.jesus.cts.asm.instruction;

import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;

public final class Label extends AsmValue {
    private String name;

    public Label(String name) {
        this.name = name;
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        builder.addLabel(name);
    }
}
