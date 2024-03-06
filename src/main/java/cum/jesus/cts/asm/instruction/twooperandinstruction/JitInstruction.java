package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.Instruction;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.*;

import java.io.PrintStream;

public final class JitInstruction extends TwoOperandInstruction {
    public JitInstruction(Operand source, Operand jumpCount) {
        super(source, jumpCount);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    jit %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Instruction inst = builder.createInstruction().opcode(Opcodes.JIT);

        if (left instanceof Register) {
            inst.operand(0, ((Register) left).getId());
        } else if (left instanceof Immediate) {
            switch (((Immediate) left).getSize()) {
                case BYTE:
                    inst.immediate(((Immediate) left).imm8());
                    break;
                case WORD:
                    inst.immediate(((Immediate) left).imm16());
                    break;
                case DWORD:
                    inst.immediate(((Immediate) left).imm32());
                    break;
                case QWORD:
                    inst.immediate(((Immediate) left).imm64());
                    break;
            }
        } else if (left instanceof Memory) {
            inst.memory((Memory) left);
        } else if (left instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) left).getIndex());
        } else if (left instanceof StringOperand) {
            inst.string(((StringOperand) left).getText());
        }

        //TODO: make it better lol
        LabelOperand label = (LabelOperand) right;
        int value = label.getValue(builder);
        inst.operand(1, OperandSize.WORD, value - builder.getPosition());
        inst.emit();
        if (value == -1) {
            label.reloc(builder, OperandSize.WORD, -3);
        }
    }
}
