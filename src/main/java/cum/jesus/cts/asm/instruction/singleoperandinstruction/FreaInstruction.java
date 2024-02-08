package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Immediate;

import java.io.PrintStream;

public final class FreaInstruction extends SingleOperandInstruction {
    public FreaInstruction(Operand count) {
        super(count);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    frea " + operand.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.FREA)
                    .operand(0, OperandSize.WORD, ((Immediate) operand).imm16())
                    .emit();
        }
    }
}
