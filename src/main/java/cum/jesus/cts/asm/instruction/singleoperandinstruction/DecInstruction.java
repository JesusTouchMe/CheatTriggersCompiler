package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Register;

import java.io.PrintStream;

public final class DecInstruction extends SingleOperandInstruction {
    public DecInstruction(Operand source) {
        super(source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    dec " + operand);
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.DEC)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        }
    }
}
