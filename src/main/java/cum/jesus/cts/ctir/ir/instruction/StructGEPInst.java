package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Memory;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.AddInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.LodInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.type.PointerType;
import cum.jesus.cts.ctir.type.StructType;
import cum.jesus.cts.ctir.type.Type;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

/**
 * Struct Get Element Pointer
 */
public final class StructGEPInst extends Instruction {
    private Type structType;
    private int structPtr;
    private int memberIndex;
    private String name;

    public StructGEPInst(Block parent, int id, Type type, Value structPtr, int memberIndex, String name) {
        super(parent.getParent().getModule(), parent, id);
        super.type = Type.getPointerType(((StructType) type.getPointerElementType()).getFieldTypes().get(memberIndex));

        this.structType = type;
        this.structPtr = structPtr.getId();
        this.memberIndex = memberIndex;
        this.name = name;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public List<Integer> getOperands() {
        return Collections.singletonList(structPtr);
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("%%%s = structGEP %s, %d", name, parent.getParent().getValue(structPtr).ident(), memberIndex);
    }

    @Override
    public String ident() {
        return String.format("%s %%%s", type.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        Operand ptrOperand = parent.getEmittedValue(structPtr);

        if (this.structType.isStructType()) {
            StructType structType = (StructType) this.structType;
            short requiredLookupOffset = 0;
            for (int i = 0; i < memberIndex; i++) {
                requiredLookupOffset += (short) structType.getFieldTypes().get(i).getSize();
            }

            if (ptrOperand instanceof Memory) {
                requiredLookupOffset += ((Memory) ptrOperand).getOffset();
                emittedValue = new Memory(((Memory) ptrOperand).getReg(), requiredLookupOffset);
            } else {
                Register reg = (Register) ptrOperand;
                emittedValue = new Memory(reg, requiredLookupOffset);
            }
        } else {
            PointerType pointerType = (PointerType) this.structType;
            StructType structType = (StructType) pointerType.getBaseType();

            short requiredLookupOffset = 0;
            for (int i = 0; i < memberIndex; i++) {
                requiredLookupOffset += (short) structType.getFieldTypes().get(i).getSize();
            }

            if (requiredLookupOffset != 0) {
                values.add(new AddInstruction(Register.get(register), ptrOperand, new Immediate(requiredLookupOffset)));
            } else {
                if (ptrOperand instanceof Memory) {
                    values.add(new LodInstruction(Register.get(register), ptrOperand));
                } else {
                    values.add(new MovInstruction(Register.get(register), ptrOperand));
                }
            }
            emittedValue = new Memory(Register.get(register));
        }
    }
}
