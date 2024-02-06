package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.type.PointerType;

import java.io.PrintStream;
import java.util.List;

public final class StoreInst extends Instruction {
    private int ptr;
    private int value;

    public StoreInst(Block parent, int id, Value ptr, Value value) {
        super(parent.getParent().getModule(), parent, id);

        this.ptr = ptr.getId();
        this.value = value.getId();

        PointerType ptrType = (PointerType) parent.getParent().getValue(this.ptr).getType();
        assert ptrType.getUnderlyingType().equals(value.getType());
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("store %s, %s", parent.getParent().getValue(ptr).ident(), parent.getParent().getValue(value).ident());
    }

    @Override
    public String ident() {
        return "%undef";
    }

    @Override
    public void emit(List<AsmValue> values) {

    }
}
