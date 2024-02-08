package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;

import java.io.PrintStream;

public final class MovZInstruction extends TwoOperandInstruction {
    public MovZInstruction(Operand dest, Operand source) {
        super(dest, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    movz %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Register lhs = (Register) left;

        if (right instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.MOVZ)
                    .operand(0, lhs.getId())
                    .operand(1, ((Register) right).getId())
                    .emit();
        } else if (right instanceof Immediate) {
            switch (((Immediate) right).getSize()) {
                case BYTE:
                    builder.createInstruction()
                            .opcode(Opcodes.MOVZ)
                            .operand(0, lhs.getId())
                            .immediate(((Immediate) right).imm8())
                            .emit();
                    break;
                case WORD:
                    builder.createInstruction()
                            .opcode(Opcodes.MOVZ)
                            .operand(0, lhs.getId())
                            .immediate(((Immediate) right).imm16())
                            .emit();
                    break;
                case DWORD:
                    builder.createInstruction()
                            .opcode(Opcodes.MOVZ)
                            .operand(2, lhs.getId())
                            .immediate(((Immediate) right).imm32())
                            .emit();

                case QWORD:
                    builder.createInstruction()
                            .opcode(Opcodes.MOVZ)
                            .operand(0, lhs.getId())
                            .immediate(((Immediate) right).imm64())
                            .emit();
                    break;
            }
        } else if (right instanceof ConstPoolEntryOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.CENT)
                    .immediate(((ConstPoolEntryOperand) right).getIndex())
                    .emit();
            builder.createInstruction()
                    .opcode(Opcodes.MOVZ)
                    .operand(0, lhs.getId())
                    .emit();
        } else if (right instanceof StringOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.MOVZ)
                    .operand(0, lhs.getId())
                    .string(((StringOperand) right).getText())
                    .emit();
        }
    }
}
