package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.twooperandinstruction.LodInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Argument;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.type.PointerType;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class LoadInst extends Instruction {
    private int ptr;
    private String name;

    public LoadInst(Block parent, int id, Value ptr, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.ptr = ptr.getId();
        this.name = name;

        if (ptr instanceof Argument) {
            super.type = ptr.getType();
        } else {
            super.type = ((PointerType) (parent.getParent().getValue(this.ptr).getType())).getUnderlyingType();
        }
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(ptr);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = load %s", name, parent.getParent().getValue(ptr).ident());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand ptrOperand = parent.getEmittedValue(ptr);

        if (ptrOperand instanceof Memory) {
            values.add(new LodInstruction(Register.get(register), ptrOperand));
        } else if (parent.getParent().getValue(ptr) instanceof Argument) {
            values.add(new MovInstruction(Register.get(register), ptrOperand));
        } else {
            Register reg = (Register) ptrOperand;
            Operand memory = new Memory(reg);
            values.add(new LodInstruction(Register.get(register), memory));
        }

        emittedValue = Register.get(register);
    }

    public Value getPointer() {
        return parent.getParent().getValue(ptr);
    }
}
