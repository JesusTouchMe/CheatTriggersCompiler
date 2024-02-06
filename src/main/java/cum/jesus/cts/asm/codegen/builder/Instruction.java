package cum.jesus.cts.asm.codegen.builder;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OutputBuffer;
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
    private Optional<ImmediateVariant> immediate;
    private Optional<String> string;
    private Optional<Integer> stackMemory;

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
        this.immediate = Optional.of(new ImmediateVariant(imm8));
        return this;
    }

    public Instruction immediate(short imm16) {
        this.immediate = Optional.of(new ImmediateVariant(imm16));
        return this;
    }

    public Instruction immediate(int imm32) {
        this.immediate = Optional.of(new ImmediateVariant(imm32));
        return this;
    }

    public Instruction immediate(long imm64) {
        this.immediate = Optional.of(new ImmediateVariant(imm64));
        return this;
    }

    public Instruction string(String str) {
        if (str.length() > 0xFFFF) {
            System.out.println("Warning: runtime string immediate is over the max length of 0xFFFF bytes long. Some of it will be cut off");
        }
        string = Optional.of(str);
        return this;
    }

    public Instruction stackMemory(long address) {
        stackMemory = Optional.of((int) address);
        return this;
    }

    public Instruction operand(int index, int b) {
        if (index < 0 || index >= operands.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + operands.length);
        }

        operands[index] = (byte) b;
        return this;
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

        if (immediate.isPresent()) {
            immediate.get().writeToOutput(output);
        } else if (string.isPresent()) {
            output.writeb(Opcodes.IMMS.getOpcode());
            output.writes((short) string.get().length());
            byte[] bytes = string.get().getBytes();
            int length = Math.min(0xFFFF, bytes.length);
            for (int i = 0; i < length; i++) {
                output.write(bytes[i]);
            }
        } else if (stackMemory.isPresent()) {
            output.writeb(Opcodes.PMEMI.getOpcode());
            output.writei(stackMemory.get());
        }

        for (byte b : buffer) {
            output.writeb(b);
        }
    }
}
