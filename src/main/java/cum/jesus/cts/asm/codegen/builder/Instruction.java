package cum.jesus.cts.asm.codegen.builder;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.util.ImmediateVariant;
import cum.jesus.cts.util.StaticList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Instruction {
    private OutputBuffer output;

    private Opcodes opcode;
    private byte[] operands;
    private List<ImmediateVariant> immediate = new ArrayList<>();
    private Optional<String> string = Optional.empty();
    private Optional<Memory> memory = Optional.empty();
    private Optional<Integer> constEntry = Optional.empty();

    Instruction(OutputBuffer output) {
        this.output = output;
    }

    public Instruction opcode(Opcodes opcode) {
        this.opcode = opcode;
        if (opcode.getCodeLength() > 0) {
            operands = new byte[opcode.getCodeLength() - 1];
            Arrays.fill(operands, (byte) 0);
        } else {
            operands = null;
        }
        return this;
    }

    public Instruction immediate(byte imm8) {
        immediate.add(new ImmediateVariant(imm8));
        return this;
    }

    public Instruction immediate(short imm16) {
        immediate.add(new ImmediateVariant(imm16));
        return this;
    }

    public Instruction immediate(int imm32) {
        immediate.add(new ImmediateVariant(imm32));
        return this;
    }

    public Instruction immediate(long imm64) {
        immediate.add(new ImmediateVariant(imm64));
        return this;
    }

    public Instruction string(String str) {
        if (str.length() > 0xFFFF) {
            System.out.println("Warning: runtime string immediate is over the max length of 0xFFFF bytes long. Some of it will be cut off");
        }
        string = Optional.of(str);
        return this;
    }

    public Instruction memory(Memory mem) {
        memory = Optional.of(mem);
        return this;
    }

    public Instruction constEntry(int constEntry) {
        this.constEntry = Optional.of(constEntry);
        return this;
    }

    public Instruction operand(int index, OperandSize size, long b) {
        switch (size) {
            case BYTE:
                if (index < 0 || index >= operands.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + operands.length);
                }

                operands[index] = (byte) b;
                break;
            case WORD:
                if (index < 0 || index + 1 >= operands.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + operands.length);
                }

                operands[index] = (byte) ((b >> 8) & 0xFF);
                operands[index + 1] = (byte) (b & 0xFF);
                break;
            case DWORD:
                if (index < 0 || index + 3 >= operands.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + operands.length);
                }

                operands[index] = (byte) ((b >> 24) & 0xFF);
                operands[index + 1] = (byte) ((b >> 16) & 0xFF);
                operands[index + 2] = (byte) ((b >> 8) & 0xFF);
                operands[index + 3] = (byte) (b & 0xFF);
                break;
            case QWORD:
                if (index < 0 || index + 7 >= operands.length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + operands.length);
                }

                operands[index] = (byte) ((b >> 56) & 0xFF);
                operands[index + 1] = (byte) ((b >> 48) & 0xFF);
                operands[index + 2] = (byte) ((b >> 40) & 0xFF);
                operands[index + 3] = (byte) ((b >> 32) & 0xFF);
                operands[index + 4] = (byte) ((b >> 24) & 0xFF);
                operands[index + 5] = (byte) ((b >> 16) & 0xFF);
                operands[index + 6] = (byte) ((b >> 8) & 0xFF);
                operands[index + 7] = (byte) (b & 0xFF);
                break;
        }

        return this;
    }

    public Instruction operand(int index, int b) {
        return operand(index, OperandSize.BYTE, b);
    }

    public void emit() {
        List<Byte> buffer;
        if (opcode.getCodeLength() > 0) {
            buffer = new StaticList<>(opcode.getCodeLength(), (byte) 0);
        } else {
            buffer = new ArrayList<>();
        }

        buffer.add(opcode.getOpcode());
        if (operands != null) {
            for (byte b : operands) {
                buffer.add(b);
            }
        }

        if (!immediate.isEmpty()) {
            for (ImmediateVariant imm : immediate) {
                imm.writeToOutput(output); //TODO all
            }
        }
        if (string.isPresent()) {
            output.writeb(Opcodes.IMMS.getOpcode());
            output.writes((short) string.get().length());
            byte[] bytes = string.get().getBytes();
            int length = Math.min(0xFFFF, bytes.length);
            for (int i = 0; i < length; i++) {
                output.write(bytes[i]);
            }
        }
        if (memory.isPresent()) {
            output.writeb(Opcodes.PMEM.getOpcode());
            output.writeb((byte) memory.get().getReg().getId());
            output.writes(memory.get().getOffset());
        }
        if (constEntry.isPresent()) {
            output.writeb(Opcodes.CENT.getOpcode());
            output.writes(constEntry.get().shortValue());
        }

        for (byte b : buffer) {
            output.writeb(b);
        }
    }
}
