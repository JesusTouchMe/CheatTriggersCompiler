package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Immediate;

public final class AlcaInstruction extends SingleOperandInstruction {
    public AlcaInstruction(Operand count) {
        super(count);
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.ALCA)
                    .immediate(((Immediate) operand).imm16())
                    .emit();
        }
    }
}
