package cum.jesus.cts.asm.codegen;

import cum.jesus.cts.asm.Types;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.FakeFunctionHandleOperand;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.StringOperand;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Stores an operand which MUST be a constant value and writes it to the constant pool
 */
public final class ConstPoolEntry {
    private final Operand operand;

    public ConstPoolEntry(Operand operand) {
        this.operand = operand;
    }

    public void writeTo(OutputStream stream) throws IOException {
         if (operand instanceof Immediate) {
             switch (((Immediate) operand).getSize()) {
                 case BYTE:
                     stream.write(Types.BYTE.toByte());
                     stream.write(((Immediate) operand).imm8());
                     break;
                 case WORD:
                     stream.write(Types.SHORT.toByte());
                     stream.write((((Immediate) operand).imm16() >> 8) & 0xFF);
                     stream.write(((Immediate) operand).imm16() & 0xFF);
                     break;
                 case DWORD:
                     stream.write(Types.INT.toByte());
                     stream.write((((Immediate) operand).imm32() >> 24) & 0xFF);
                     stream.write((((Immediate) operand).imm32() >> 16) & 0xFF);
                     stream.write((((Immediate) operand).imm32() >> 8) & 0xFF);
                     stream.write(((Immediate) operand).imm32() & 0xFF);
                     break;
                 case QWORD:
                     stream.write(Types.LONG.toByte());
                     stream.write((int) ((((Immediate) operand).imm64() >> 56) & 0xFF));
                     stream.write((int) ((((Immediate) operand).imm64() >> 48) & 0xFF));
                     stream.write((int) ((((Immediate) operand).imm64() >> 40) & 0xFF));
                     stream.write((int) ((((Immediate) operand).imm64() >> 32) & 0xFF));
                     stream.write((int) ((((Immediate) operand).imm64() >> 24) & 0xFF));
                     stream.write((int) ((((Immediate) operand).imm64() >> 16) & 0xFF));
                     stream.write((int) ((((Immediate) operand).imm64() >> 8) & 0xFF));
                     stream.write((int) (((Immediate) operand).imm64() & 0xFF));
                     break;
             }
         } else if (operand instanceof StringOperand) {
             stream.write(Types.STRING.toByte());
             stream.write((((StringOperand) operand).getText().length() >> 8) & 0xFF);
             stream.write(((StringOperand) operand).getText().length() & 0xFF);
             byte[] bytes = ((StringOperand) operand).getText().getBytes();
             int length = Math.min(0xFFFF, bytes.length);
             for (int i = 0; i < length; i++) {
                 stream.write(bytes[i]);
             }
         } else if (operand instanceof FakeFunctionHandleOperand) {
             stream.write(Types.FUNCTION.toByte());
             stream.write((((FakeFunctionHandleOperand) operand).getName().length() >> 8) & 0xFF);
             stream.write(((FakeFunctionHandleOperand) operand).getName().length() & 0xFF);
             byte[] bytes = ((FakeFunctionHandleOperand) operand).getName().getBytes();
             int length = Math.min(0xFFFF, bytes.length);
             for (int i = 0; i < length; i++) {
                 stream.write(bytes[i]);
             }
         } else if (operand instanceof ConstPoolEntryOperand) {
             stream.write(Types.CONSTANT_LOAD.toByte());
             stream.write((((ConstPoolEntryOperand) operand).getIndex() >> 8) & 0xFF);
             stream.write(((ConstPoolEntryOperand) operand).getIndex() & 0xFF);
         }
    }
}
