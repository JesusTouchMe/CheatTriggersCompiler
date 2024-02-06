package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.LabelOperand;
import cum.jesus.cts.asm.instruction.operand.Register;

public final class JmpInstruction extends SingleOperandInstruction {
    public JmpInstruction(Operand count) {
        super(count);
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof LabelOperand) {
            int value = ((LabelOperand) operand).getValue(builder);
            builder.createInstruction()
                    .opcode(Opcodes.JMP)
                    .immediate((short) (value - builder.getPosition() - 2))
                    .emit();
            if (value == -1) {
                ((LabelOperand) operand).reloc(builder, OperandSize.WORD, -1);
            }
        } else if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.JMV)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        } else if (operand instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.JMP)
                    .immediate(((Immediate) operand).imm16())
                    .emit();
        }
    }
}
