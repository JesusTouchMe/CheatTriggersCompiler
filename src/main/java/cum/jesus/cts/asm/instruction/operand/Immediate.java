package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.instruction.Operand;

public class Immediate extends Operand {
    private final long value;

    public Immediate(long value) {
        this.value = value;
    }

    public byte imm8() {
        return (byte) value;
    }

    public short imm16() {
        return (short) value;
    }

    public int imm32() {
        return (int) value;
    }

    public long imm64() {
        return value;
    }

    public OperandSize getSize() {
        if (value <= 0xFF) {
            return OperandSize.BYTE;
        } else if (value <= 0xFFFF) {
            return OperandSize.WORD;
        } else if (value <= 0xFFFFFFFFL) {
            return OperandSize.DWORD;
        } else {
            return OperandSize.QWORD;
        }
    }

    @Override
    public String ident() {
        return String.valueOf(value);
    }

    @Override
    public Operand clone() {
        return new Immediate(value);
    }
}
