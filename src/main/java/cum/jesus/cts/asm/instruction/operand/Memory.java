package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.instruction.Operand;

public final class Memory extends Operand {
    private Register addressRegister;
    private int offset;

    public Memory(Register addressRegister, int offset) {
        this.addressRegister = addressRegister;
        this.offset = offset;
    }

    public Memory(Register addressRegister) {
        this(addressRegister, 0);
    }

    public Register getReg() {
        return addressRegister;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public Operand clone() {
        return new Memory((Register) addressRegister.clone(), offset);
    }
}
