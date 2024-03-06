package cum.jesus.cts.asm.instruction.twooperandinstruction;

import cum.jesus.cts.asm.codegen.Opcodes;
import cum.jesus.cts.asm.codegen.builder.Instruction;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.TwoOperandInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;

import java.io.PrintStream;

public final class CallInstruction extends TwoOperandInstruction {
    public CallInstruction(Operand mod, Operand func) {
        super(mod, func);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("    call %s, %s\n", left.ident(), right.ident());
    }

    @Override
    public void emit(OpcodeBuilder builder) {
        Instruction inst = builder.createInstruction()
                .opcode(Opcodes.CALL);

        if (left instanceof Register) {
            inst.operand(0, ((Register) left).getId());
        } else if (left instanceof Memory) {
            inst.memory((Memory) left);
        } else if (left instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) left).getIndex());
        } else if (left instanceof StringOperand) {
            inst.string(((StringOperand) left).getText());
        }

        if (right instanceof Register) {
            inst.operand(1, ((Register) right).getId());
        } else if (right instanceof Memory) {
            inst.memory((Memory) right);
        } else if (right instanceof ConstPoolEntryOperand) {
            inst.constEntry(((ConstPoolEntryOperand) right).getIndex());
        } else if (right instanceof StringOperand) {
            inst.string(((StringOperand) right).getText());
        }

        inst.emit();
    }
}
