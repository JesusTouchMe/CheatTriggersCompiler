package cum.jesus.cts.asm.instruction.threeoperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.Instruction;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.ThreeOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.*;

import java.io.PrintStream;

public abstract class LogicalInstruction extends ThreeOperandInstruction {
    private Opcodes opcode;
    private String ident;

    protected LogicalInstruction(Opcodes opcode, String ident, Operand dest, Operand left, Operand right) {
        super(dest, left, right);

        this.opcode = opcode;
        this.ident = ident;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    %s %s, %s, %s\n", ident, first.ident(), second.ident(), third.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Register dest = (Register) first;

        Instruction inst = builder.createInstruction()
                .opcode(opcode)
                .operand(0, dest.getId());

        if (second instanceof Register) {
            inst.operand(1, ((Register) second).getId());
        } else if (second instanceof Immediate) {
            switch (((Immediate) second).getSize()) {
                case BYTE:
                    inst.immediate(((Immediate) second).imm8());
                    break;
                case WORD:
                    inst.immediate(((Immediate) second).imm16());
                    break;
                case DWORD:
                    inst.immediate(((Immediate) second).imm32());
                    break;
                case QWORD:
                    inst.immediate(((Immediate) second).imm64());
                    break;
            }
        } else if (second instanceof Memory) {
            inst.memory((Memory) second);
        } else if (second instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) second).getIndex());
        } else if (second instanceof StringOperand) {
            inst.string(((StringOperand) second).getText());
        }

        if (third instanceof Register) {
            inst.operand(2, ((Register) third).getId())
                    .emit();
        } else if (third instanceof Immediate) {
            switch (((Immediate) third).getSize()) {
                case BYTE:
                    inst.immediate(((Immediate) third).imm8())
                            .emit();
                    break;
                case WORD:
                    inst.immediate(((Immediate) third).imm16())
                            .emit();
                    break;
                case DWORD:
                    inst.immediate(((Immediate) third).imm32())
                            .emit();
                    break;
                case QWORD:
                    inst.immediate(((Immediate) third).imm64())
                            .emit();
                    break;
            }
        } else if (third instanceof Memory) {
            inst.memory((Memory) third)
                    .emit();
        } else if (third instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) third).getIndex())
                    .emit();
        } else if (third instanceof StringOperand) {
            inst.string(((StringOperand) third).getText())
                    .emit();
        }
    }
}
