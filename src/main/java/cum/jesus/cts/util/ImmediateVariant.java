package cum.jesus.cts.util;

import cum.jesus.cts.asm.Types;
import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.util.exceptions.UnreachableStatementException;

public final class ImmediateVariant {
    private byte b;
    private short s;
    private int i;
    private long l;

    private final Types type;

    public ImmediateVariant(byte b) {
        this.b = b;
        this.type = Types.BYTE;
    }

    public ImmediateVariant(short s) {
        this.s = s;
        this.type = Types.SHORT;
    }

    public ImmediateVariant(int i) {
        this.i = i;
        this.type = Types.INT;
    }

    public ImmediateVariant(long l) {
        this.l = l;
        this.type = Types.LONG;
    }

    public void writeToOutput(OutputBuffer output) {
        switch (type) {
            case BYTE:
                output.writeb(Opcodes.IMM8.getOpcode());
                output.writeb(type.toByte()); // byte type
                output.writeb(b);
                break;
            case SHORT:
                output.writeb(Opcodes.IMM16.getOpcode());
                output.writeb(type.toByte()); // short type
                output.writes(s);
                break;
            case INT:
                output.writeb(Opcodes.IMM32.getOpcode());
                output.writeb(type.toByte()); // int type
                output.writei(i);
                break;
            case LONG:
                output.writeb(Opcodes.IMM64.getOpcode());
                output.writeb(type.toByte()); // long type
                output.writel(l);
                break;

            default:
                throw UnreachableStatementException.INSTANCE;
        }
    }
}
