package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.AddInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class AddrOfInst extends Instruction {
    private int ptr;
    private String name;

    public AddrOfInst(Block parent, int id, AllocaInst ptr, String name) {
        super(parent.getParent().getModule(), parent, id);
        super.type = ptr.getAllocatedType();

        this.ptr = ptr.getId();
        this.name = name;
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
        stream.printf("%%%s = addr %s", name, parent.getParent().getValue(ptr).ident());
    }

    @Override
    public String ident() {
        return "%" + name;
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand ptrOperand = parent.getEmittedValue(ptr);

        if (ptrOperand instanceof Memory) {
            values.add(new AddInstruction(Register.get(register), ((Memory) ptrOperand).getReg(), new Immediate(((Memory) ptrOperand).getOffset())));
            emittedValue = Register.get(register);
        } else {
            throw new RuntimeException("todo");
        }
    }

    public Value getPointer() {
        return parent.getParent().getValue(ptr);
    }
}
