package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.type.PointerType;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

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

    public void setStackOffset(Function.AllocaSignature signature, int stackOffset) {
        Objects.requireNonNull(signature);
        this.stackOffset = stackOffset;
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

    @Override
    public void emit(List<AsmValue> values) {

    }
}
