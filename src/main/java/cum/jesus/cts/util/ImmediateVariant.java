package cum.jesus.cts.util;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

public final class ImmediateVariant {
    private byte b;
    private short s;
    private int i;
    private long l;

    /**
     * 1 = byte
     * 2 = short
     * 3 = int
     * 4 = long
     */
    private final byte type;

    public ImmediateVariant(byte b) {
        this.b = b;
        this.type = 1;
    }

    public ImmediateVariant(short s) {
        this.s = s;
        this.type = 2;
    }

    public ImmediateVariant(int i) {
        this.i = i;
        this.type = 3;
    }

    public ImmediateVariant(long l) {
        this.l = l;
        this.type = 4;
    }

    public void writeToOutput(OutputBuffer output) {
        switch (type) {
            case 1:
                output.writeb(Opcodes.IMM8.getOpcode());
                output.writeb((byte) 0x01); // byte type
                output.writeb(b);
                break;
            case 2:
                output.writeb(Opcodes.IMM16.getOpcode());
                output.writeb((byte) 0x02); // short type
                output.writes(s);
                break;
            case 3:
                output.writeb(Opcodes.IMM32.getOpcode());
                output.writeb((byte) 0x03); // int type
                output.writei(i);
                break;
            case 4:
                output.writeb(Opcodes.IMM64.getOpcode());
                output.writeb((byte) 0x04); // long type
                output.writel(l);
                break;

            default:
                throw UnreachableStatementException.INSTANCE;
        }
    }
}
