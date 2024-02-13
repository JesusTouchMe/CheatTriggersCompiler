package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Register;

import java.io.PrintStream;

public final class PopInstruction extends SingleOperandInstruction {
    public PopInstruction(Operand dest) {
        super(dest);
    }

    @Override
    public void print(PrintStream stream) {
        if (operand == null) {
            stream.println("    pop");
        } else {
            stream.println("    pop " + operand.ident());
        }
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand == null) {
            builder.createInstruction()
                    .opcode(Opcodes.POP)
                    .emit();
        } else if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.POP)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        }
    }
}
