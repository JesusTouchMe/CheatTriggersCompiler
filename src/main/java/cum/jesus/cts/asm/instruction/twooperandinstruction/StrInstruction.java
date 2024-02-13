package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.Instruction;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.*;

import java.io.PrintStream;

public final class StrInstruction extends TwoOperandInstruction {
    public StrInstruction(Operand dest, Operand source) {
        super(dest, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    str %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (left instanceof Memory) {
            handleLeftMemory(builder, (Memory) left);
        } else if (left instanceof Register) {
            handleLeftRegister(builder, (Register) left);
        }
    }

    private void handleLeftMemory(OpcodeBuilder builder, Memory mem) {
        Instruction inst = builder.createInstruction()
                .opcode(Opcodes.STR)
                .operand(0, mem.getReg().getId())
                .operand(1, OperandSize.WORD, mem.getOffset());

        if (right instanceof Register) {
            inst.operand(3, ((Register) right).getId())
                    .emit();
        } else if (right instanceof Immediate) {
            switch (((Immediate) right).getSize()) {
                case BYTE:
                   inst.immediate(((Immediate) right).imm8())
                           .emit();
                    break;
                case WORD:
                    inst.immediate(((Immediate) right).imm16())
                            .emit();
                    break;
                case DWORD:
                    inst.immediate(((Immediate) right).imm32())
                            .emit();
                    break;
                case QWORD:
                    inst.immediate(((Immediate) right).imm64())
                            .emit();
                    break;
            }
        } else if (right instanceof Memory) {
            inst.memory((Memory) right)
                    .emit();
        } else if (right instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) right).getIndex())
                    .emit();
        } else if (right instanceof StringOperand) {
            inst.string(((StringOperand) right).getText())
                    .emit();
        }
    }

    private void handleLeftRegister(OpcodeBuilder builder, Register reg) {
        Instruction inst = builder.createInstruction()
                .opcode(Opcodes.STR)
                .operand(0, reg.getId());

        if (right instanceof Register) {
            inst.operand(3, ((Register) right).getId())
                    .emit();
        } else if (right instanceof Immediate) {
            switch (((Immediate) right).getSize()) {
                case BYTE:
                    inst.immediate(((Immediate) right).imm8())
                            .emit();
                    break;
                case WORD:
                    inst.immediate(((Immediate) right).imm16())
                            .emit();
                    break;
                case DWORD:
                    inst.immediate(((Immediate) right).imm32())
                            .emit();
                    break;
                case QWORD:
                    inst.immediate(((Immediate) right).imm64())
                            .emit();
                    break;
            }
        } else if (right instanceof Memory) {
            inst.memory((Memory) right)
                    .emit();
        } else if (right instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) right).getIndex())
                    .emit();
        } else if (right instanceof StringOperand) {
            inst.string(((StringOperand) right).getText())
                    .emit();
        }
    }
}
