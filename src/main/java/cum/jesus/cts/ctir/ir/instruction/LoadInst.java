package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.type.PointerType;

import java.io.PrintStream;

public final class LoadInst extends Instruction {
    private int ptr;
    private String name;

    public LoadInst(Block parent, int id, Value ptr, String name) {
        super(parent.getParent().getModule(), parent, id);

        this.ptr = ptr.getId();
        this.name = name;

        super.type = ((PointerType) (parent.getParent().getValue(this.ptr).getType())).getUnderlyingType();
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = load %s", name, parent.getParent().getValue(ptr).ident());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    public Value getPointer() {
        return parent.getParent().getValue(ptr);
    }
}
