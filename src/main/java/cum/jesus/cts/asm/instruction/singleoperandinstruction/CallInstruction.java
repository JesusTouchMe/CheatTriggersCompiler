package cum.jesus.cts.asm.instruction.singleoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.SingleOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;

import java.io.PrintStream;

public final class CallInstruction extends SingleOperandInstruction {
    public CallInstruction(Operand source) {
        super(source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println("    call " + operand.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        if (operand instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.CALL)
                    .operand(0, ((Register) operand).getId())
                    .emit();
        } else if (operand instanceof StringOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.CALL)
                    .string(((StringOperand) operand).getText())
                    .emit();
        } else if (operand instanceof ConstPoolEntryOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.CENT)
                    .immediate(((ConstPoolEntryOperand) operand).getIndex())
                    .emit();
            builder.createInstruction()
                    .opcode(Opcodes.CALL)
                    .emit();
        }
    }
}
