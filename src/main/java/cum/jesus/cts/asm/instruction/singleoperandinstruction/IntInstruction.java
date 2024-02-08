package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Immediate;

import java.io.PrintStream;

public final class IntInstruction extends SingleOperandInstruction {
    private final byte[] bytes;

    public IntInstruction(Operand intCode, int byte2, int byte3, int byte4) {
        super(intCode);
        bytes = new byte[3];

        bytes[0] = (byte) byte2;
        bytes[1] = (byte) byte3;
        bytes[2] = (byte) byte4;
    }

    public IntInstruction(Operand intCode) {
        this(intCode, 0, 0, 0);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    int 0x%-2x", Integer.parseInt(operand.ident()));
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.INT)
                    .operand(0, ((Immediate) operand).imm8())
                    .operand(1, bytes[0])
                    .operand(2, bytes[1])
                    .operand(3, bytes[2])
                    .emit();
        }
    }
}
