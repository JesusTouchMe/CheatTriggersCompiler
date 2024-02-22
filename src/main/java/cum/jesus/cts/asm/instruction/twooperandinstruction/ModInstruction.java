package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.*;

import java.io.PrintStream;

public class ModInstruction extends TwoOperandInstruction {
    public ModInstruction(Operand dest, Operand source) {
        super(dest, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    mod %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Register lhs = (Register) left;

        if (right instanceof Register) {
            builder.createInstruction()
                    .opcode(Opcodes.MOD)
                    .operand(0, lhs.getId())
                    .operand(1, ((Register) right).getId())
                    .emit();
        } else if (right instanceof Memory) {
            builder.createInstruction()
                    .opcode(Opcodes.MOD)
                    .operand(0, lhs.getId())
                    .memory((Memory) right)
                    .emit();
        } else if (right instanceof ConstPoolEntryOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.MOD)
                    .operand(0, lhs.getId())
                    .constEntry(((ConstPoolEntryOperand) right).getIndex())
                    .emit();
        } else if (right instanceof StringOperand) {
            builder.createInstruction()
                    .opcode(Opcodes.MOD)
                    .operand(0, lhs.getId())
                    .string(((StringOperand) right).getText())
                    .emit();
        }
    }
}
