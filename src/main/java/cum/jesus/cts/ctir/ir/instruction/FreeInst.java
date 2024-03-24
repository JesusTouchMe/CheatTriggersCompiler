package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.FreInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public final class FreeInst extends Instruction {
    private int ptr;

    public FreeInst(Block parent, int id, Value ptr) {
        super(parent.getParent().getModule(), parent, id);

        this.ptr = ptr.getId();
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(ptr);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("free %s", parent.getParent().getValue(ptr).ident());
    }

    @Override
    public String ident() {
        return "%undef";
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand ptrOperand = parent.getEmittedValue(ptr);

        values.add(new FreInstruction(ptrOperand));
    }
}
