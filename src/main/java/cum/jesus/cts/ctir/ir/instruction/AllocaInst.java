package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.type.PointerType;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;

public final class AllocaInst extends Instruction {
    private String name;
    private Type allocatedType;
    private int stackOffset;

    public AllocaInst(Block parent, int id, Type allocatedType, String name) {
        super(parent.getParent().getModule(), parent, id);
        super.type = PointerType.get(allocatedType);

        this.name = name;
        this.allocatedType = allocatedType;
    }

    public Type getAllocatedType() {
        return allocatedType;
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = alloca %s", name, allocatedType.getName());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }
}
