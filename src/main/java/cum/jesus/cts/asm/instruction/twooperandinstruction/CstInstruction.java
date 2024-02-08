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

public final class CstInstruction extends TwoOperandInstruction {
    public CstInstruction(Operand index, Operand source) {
        super(index, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    cst %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Register rhs = (Register) right;

        if (left instanceof ConstPoolEntryOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.CST)
                    .operand(0, OperandSize.WORD, ((ConstPoolEntryOperand) left).getIndex())
                    .operand(2, rhs.getId())
                    .emit();
        } else if (left instanceof Immediate) {
            builder.createInstruction()
                    .opcode(Opcodes.CST)
                    .operand(0, OperandSize.WORD, ((Immediate) left).imm16())
                    .operand(2, rhs.getId())
                    .emit();
        }
    }
}
