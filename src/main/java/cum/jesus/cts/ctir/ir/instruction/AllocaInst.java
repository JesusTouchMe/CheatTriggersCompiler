package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.StackMemory;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.type.PointerType;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AllocaInst extends Instruction {
    private String name;
    private Type allocatedType;
    private short stackOffset;

    public AllocaInst(Block parent, int id, Type allocatedType, String name) {
        super(parent.getParent().getModule(), parent, id);
        super.type = PointerType.get(allocatedType);

        this.name = name;
        this.allocatedType = allocatedType;
    }

    public void setStackOffset(Function.AllocaSignature signature, short stackOffset) {
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
    public List<Integer> getOperands() {
        return new ArrayList<>();
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
        emittedValue = new StackMemory(stackOffset);
    }

    @Override
    public Operand getEmittedValue() {
        return emittedValue.clone();
    }
}
