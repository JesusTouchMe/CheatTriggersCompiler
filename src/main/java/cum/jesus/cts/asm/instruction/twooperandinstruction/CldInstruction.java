package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;

import java.io.PrintStream;

public final class CldInstruction extends TwoOperandInstruction {
    public CldInstruction(Operand dest, Operand index) {
        super(dest, index);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    cld %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Register lhs = (Register) left;

        if (right instanceof ConstPoolEntryOperand) {
             builder.createInstruction()
                     .opcode(Opcodes.CLD)
                     .operand(0, lhs.getId())
                     .operand(1, OperandSize.WORD, ((ConstPoolEntryOperand) right).getIndex())
                     .emit();
        } else if (right instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.CLD)
                    .operand(0, lhs.getId())
                    .operand(1, OperandSize.WORD, ((Immediate) right).imm16())
                    .emit();
        }
    }
}
