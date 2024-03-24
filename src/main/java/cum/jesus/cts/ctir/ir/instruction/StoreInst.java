package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CstInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.StrInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.type.StructType;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public final class StoreInst extends Instruction {
    private int ptr;
    private int value;

    public StoreInst(Block parent, int id, Value ptr, Value value) {
        super(parent.getParent().getModule(), parent, id);

        this.ptr = ptr.getId();
        this.value = value.getId();

        assert parent.getParent().getValue(this.ptr).getType().getPointerElementType().equals(value.getType());
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public List<Integer> getOperands() {
        return Arrays.asList(ptr, value);
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
        Value ptrValue = parent.getParent().getValue(ptr);
        Operand ptrOperand = parent.getEmittedValue(ptr);
        Operand valueOperand = parent.getEmittedValue(value);

        if (ptrValue instanceof AllocaInst && ((AllocaInst) ptrValue).getAllocatedType().isStructType()) {
            StructType structType = (StructType) ((AllocaInst) ptrValue).getAllocatedType();
            storeStruct(structType, values, 0);
        } else if (ptrOperand instanceof Register) {
            values.add(new MovInstruction(ptrOperand, valueOperand));
        } if (ptrOperand instanceof ConstPoolEntryOperand) {
            values.add(new CstInstruction(ptrOperand, valueOperand));
        } else {
            values.add(new StrInstruction(ptrOperand, valueOperand));
        }
    }

    private void storeStruct(StructType structType, List<AsmValue> values, int offset) {
        AllocaInst ptrValue = (AllocaInst) parent.getParent().getValue(ptr);
        Memory ptrOperand = (Memory) parent.getEmittedValue(ptr);
        Operand valueOperand = parent.getEmittedValue(value);

        for (Type type : structType.getFieldTypes()) {
            if (type.isStructType()) {
                storeStruct((StructType) type, values, offset);
            } else {
                if (valueOperand instanceof Register) {
                    values.add(new StrInstruction(new Memory(ptrOperand.getReg(), (short) (ptrOperand.getOffset() + offset)), new Memory((Register) valueOperand, (short) offset)));
                } else {
                    values.add(new StrInstruction(new Memory(ptrOperand.getReg(), (short) (ptrOperand.getOffset() + offset)), new Memory(((Memory) valueOperand).getReg(), (short) (((Memory) valueOperand).getOffset() + offset))));
                }
            }

            offset += type.getSize();
        }
    }
}
