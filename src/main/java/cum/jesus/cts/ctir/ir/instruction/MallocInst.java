package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.MulInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.AlcInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.type.PointerType;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

/**
 * Unlike {@link AllocaInst} which represents stack memory, this represents heap memory
 */
public final class MallocInst extends Instruction {
    private String name;
    private Type allocatedType;
    private int count;

    public MallocInst(Block parent, int id, Type allocatedType, Value count, String name) {
        super(parent.getParent().getModule(), parent, id);
        super.type = PointerType.get(allocatedType);

        this.name = name;
        this.allocatedType = allocatedType;
        this.count = count.getId();
    }

    public Type getAllocatedType() {
        return allocatedType;
    }

    @Override
    public boolean requiresRegister() {
        return true; // for storing the allocated memory
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(count);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = malloc %s, %s", name, allocatedType.getName(), parent.getParent().getValue(count).ident());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand countOperand = parent.getEmittedValue(count);

        if (type.getSize() == 1) { // if the type size is only 1, we can skip the multiplication and allocate the count
            values.add(new AlcInstruction(Register.get(register), countOperand));
        } else if (countOperand instanceof Immediate) {
            if (((Immediate) countOperand).imm32() <= 0) {
                throw new RuntimeException("Malloc nothing");
            }

            if (((Immediate) countOperand).imm32() == 1) { // if there's only 1, simply allocate its size
                values.add(new AlcInstruction(Register.get(register), new Immediate(allocatedType.getSize())));
            } else {
                values.add(new MulInstruction(Register.get(register), new Immediate(allocatedType.getSize()), countOperand));
                values.add(new AlcInstruction(Register.get(register), Register.get(register)));
            }
        } else {
            values.add(new MulInstruction(Register.get(register), new Immediate(allocatedType.getSize()), countOperand));
            values.add(new AlcInstruction(Register.get(register), Register.get(register)));
        }

        emittedValue = Register.get(register);
    }
}
