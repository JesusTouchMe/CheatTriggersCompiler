package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.*;

import java.io.PrintStream;

public final class PushInstruction extends SingleOperandInstruction {
    public PushInstruction(Operand source) {
        super(source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    push " + operand.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.PUSH)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        } else if (operand instanceof Immediate) {
            switch (((Immediate) operand).getSize()) {
                case BYTE:
                    builder.createInstruction()
                            .opcode(Opcodes.PUSH)
                            .immediate(((Immediate) operand).imm8())
                            .emit();
                    break;
                case WORD:
                    builder.createInstruction()
                            .opcode(Opcodes.PUSH)
                            .immediate(((Immediate) operand).imm16())
                            .emit();
                    break;
                case DWORD:
                    builder.createInstruction()
                            .opcode(Opcodes.PUSH)
                            .immediate(((Immediate) operand).imm32())
                            .emit();
                    break;
                case QWORD:
                    builder.createInstruction()
                            .opcode(Opcodes.PUSH)
                            .immediate(((Immediate) operand).imm64())
                            .emit();
                    break;
            }
        } else if (operand instanceof Memory) {
            builder.createInstruction()
                    .opcode(Opcodes.PUSH)
                    .memory((Memory) operand)
                    .emit();
        } else if (operand instanceof ConstPoolEntryOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.PUSH)
                    .constEntry(((ConstPoolEntryOperand) operand).getIndex())
                    .emit();
        } else if (operand instanceof StringOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.PUSH)
                    .string(((StringOperand) operand).getText())
                    .emit();
        }
    }
}
