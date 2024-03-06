package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.LabelOperand;
import cum.jesus.cts.asm.instruction.operand.Register;

import java.io.PrintStream;

public final class JmpInstruction extends SingleOperandInstruction {
    public JmpInstruction(Operand count) {
        super(count);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    jmp " + operand.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof LabelOperand) {
            int value = ((LabelOperand) operand).getValue(builder);
            builder.createInstruction()
                    .opcode(Opcodes.JMP)
                    .operand(0, OperandSize.WORD, value - builder.getPosition())
                    .emit();
            if (value == -1) {
                ((LabelOperand) operand).reloc(builder, OperandSize.WORD, -4);
            }
        } else if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.JMV)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        } else if (operand instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.JMP)
                    .operand(0, OperandSize.WORD, ((Immediate) operand).imm16())
                    .emit();
        }
    }
}
