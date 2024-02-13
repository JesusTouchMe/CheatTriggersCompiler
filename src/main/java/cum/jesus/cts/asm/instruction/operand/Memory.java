package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.instruction.Operand;

public class Memory extends Operand {
    private Register addressRegister;
    private short offset;

    public Memory(Register addressRegister, short offset) {
        this.addressRegister = addressRegister;
        this.offset = offset;
    }

    public Memory(Register addressRegister) {
        this(addressRegister, (short) 0);
    }

    public Register getReg() {
        return addressRegister;
    }

    public short getOffset() {
        return offset;
    }

    @Override
    public String ident() {
        if (offset == 0) {
            return "[" + addressRegister.ident() + "]";
        } else {
            if (offset > 0) {
                return '[' + addressRegister.ident() + '+' + offset + ']';
            } else {
                return '[' + addressRegister.ident() + offset + ']';
            }
        }
    }

    @Override
    public Operand clone() {
        return new Memory((Register) addressRegister.clone(), offset);
    }
}
