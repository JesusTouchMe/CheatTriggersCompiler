package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.instruction.Operand;

public final class ConstPoolEntryOperand extends Operand {
    private final int index;

    public ConstPoolEntryOperand(int index) {
        this.index = index;
    }

    public short getIndex() {
        return (short) index;
    }

    @Override
    public String ident() {
    return "#" + index;
    }

    @Override
    public Operand clone() {
        return new ConstPoolEntryOperand(index);
    }
}
