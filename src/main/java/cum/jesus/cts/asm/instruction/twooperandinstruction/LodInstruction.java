package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;

import java.io.PrintStream;

public final class LodInstruction extends TwoOperandInstruction {
    public LodInstruction(Operand dest, Operand source) {
        super(dest, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    lod %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Register lhs = (Register) left;

        if (right instanceof Memory) {
            builder.createInstruction()
                    .opcode(Opcodes.LOD)
                    .operand(0, lhs.getId())
                    .operand(1, ((Memory) right).getReg().getId())
                    .operand(2, OperandSize.WORD, ((Memory) right).getOffset())
                    .emit();
        } else if (right instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.LOD)
                    .operand(0, lhs.getId())
                    .operand(1, ((Register) right).getId())
                    .emit();
        }
    }
}
