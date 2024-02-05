package cum.jesus.cts.asm.codegen.builder;

import cum.jesus.cts.asm.codegen.OperandSize;

public final class ForwardLabel {
    public String name;
    public OperandSize size;
    public int offset;
    public int position;

    public ForwardLabel(String name, OperandSize size, int offset, int position) {
        this.name = name;
        this.size = size;
        this.offset = offset;
        this.position = position;
    }
}
