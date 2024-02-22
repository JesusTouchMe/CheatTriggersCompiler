package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.Instruction;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.ThreeOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;

import java.io.PrintStream;

public class FunInstruction extends ThreeOperandInstruction {
    public FunInstruction(Operand dest, Operand module, Operand source) {
        super(dest, module, source);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    fun %s, %s, %s\n", first.ident(), second.ident(), third.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Instruction inst = builder.createInstruction()
                .opcode(Opcodes.FUN)
                .operand(0, ((Register) first).getId());

        if (second instanceof Register) {
            inst.operand(1, ((Register) second).getId());
        } else if (second instanceof Memory) {
            inst.memory((Memory) second);
        } else if (second instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) second).getIndex());
        } else if (second instanceof StringOperand) {
            inst.string(((StringOperand) second).getText());
        }

        if (third instanceof Register) {
            inst.operand(2, ((Register) third).getId());
        } else if (third instanceof Memory) {
            inst.memory((Memory) third);
        } else if (third instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) third).getIndex());
        } else if (third instanceof StringOperand) {
            inst.string(((StringOperand) third).getText());
        }
    }
}
