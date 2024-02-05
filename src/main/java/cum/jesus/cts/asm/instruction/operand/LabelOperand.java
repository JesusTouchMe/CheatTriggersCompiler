package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;

public final class LabelOperand extends Immediate {
    private String name;
    private String location;

    public LabelOperand(String name, String location) {
        super(0);
        this.name = name;
        this.location = location;
    }

    public LabelOperand(String name) {
        this(name, "");
    }

    public void reloc(OpcodeBuilder builder, OperandSize size, int offset) {
        if (!name.equals("$")) {
            if (builder.getLabel(name) == -1) {
                builder.forwardLabel(name, size, offset);
            }
        }
    }

    public int getValue(OpcodeBuilder builder) {
        if (name.equals("$")) {
            return builder.getPosition();
        }
        return builder.getLabel(name);
    }

    @Override
    public Operand clone() {
        return new LabelOperand(name, location);
    }
}
