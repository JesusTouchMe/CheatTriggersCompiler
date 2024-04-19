package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.AddInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.type.ArrayType;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public final class ArrayGEPInst extends Instruction {
    private Type arrayType;
    private int arrayPtr;
    private int elementIndex;
    private String name;

    public ArrayGEPInst(Block parent, int id, Type arrayType, Value arrayPtr, Value elementIndex, String name) {
        super(parent.getParent().getModule(), parent, id);
        super.type = arrayType.getPointerElementType();

        this.arrayType = arrayType;
        this.arrayPtr = arrayPtr.getId();
        this.elementIndex = elementIndex.getId();
        this.name = name;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public List<Integer> getOperands() {
        return Arrays.asList(arrayPtr, elementIndex);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = arrayGEP %s, %s", name, parent.getParent().getValue(arrayPtr).ident(), parent.getParent().getValue(elementIndex).ident());
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand ptrOperand = parent.getEmittedValue(arrayPtr);
        Operand indexOperand = parent.getEmittedValue(elementIndex);

        if (parent.getParent().getValue(arrayPtr) instanceof AllocaInst
                && ((AllocaInst) parent.getParent().getValue(arrayPtr)).getAllocatedType() instanceof ArrayType
                && indexOperand instanceof Immediate && ptrOperand instanceof Memory) {
            emittedValue = new Memory(((Memory) ptrOperand).getReg(), (short) (((Memory) ptrOperand).getOffset() + ((Immediate) indexOperand).imm16()));
        } else {
            values.add(new AddInstruction(Register.get(register), ptrOperand, indexOperand));
            emittedValue = new Memory(Register.get(register));
        }
    }
}
