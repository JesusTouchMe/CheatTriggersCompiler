package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Register;

public final class IncInstruction extends SingleOperandInstruction {
    public IncInstruction(Operand source) {
        super(source);
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.INC)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        }
    }
}
