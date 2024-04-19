package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.constant.ConstantInt;
import cum.jesus.cts.ctir.ir.constant.ConstantString;
import cum.jesus.cts.ctir.ir.instruction.*;
import cum.jesus.cts.ctir.ir.misc.InlineAsm;
import cum.jesus.cts.ctir.ir.misc.SaveStackOffset;
import cum.jesus.cts.ctir.ir.misc.SetStackOffset;
import cum.jesus.cts.ctir.type.FunctionType;
import cum.jesus.cts.ctir.type.Type;

import java.util.List;

public final class Builder {
    Block insertPoint;

    public Block getInsertPoint() {
        return insertPoint;
    }

    public void setInsertPoint(Block insertPoint) {
        this.insertPoint = insertPoint;
    }

    public RetInst createRet(Value returnValue) {
        int id = insertPoint.getParent().getValueCount();
        RetInst ret = new RetInst(insertPoint, id, returnValue);

        insertPoint.insertValue(ret);
        insertPoint.getParent().addValue(ret);

        return ret;
    }

    public CallInst createCall(Module module, FunctionType functionType, Value callee, List<Value> parameters, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        CallInst call = new CallInst(insertPoint, id, name, module, functionType, callee, parameters);

        insertPoint.insertValue(call);
        insertPoint.getParent().addValue(call);

        return call;
    }

    public CallInst createCall(Module module, FunctionType functionType, Value callee, List<Value> parameters) {
        return createCall(module, functionType, callee, parameters, "");
    }

    public AllocaInst createAlloca(Type allocatedType, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        AllocaInst alloca = new AllocaInst(insertPoint, id, allocatedType, name);

        insertPoint.insertValue(alloca);
        insertPoint.getParent().addValue(alloca);

        return alloca;
    }

    public AllocaInst createAlloca(Type allocatedType) {
        return createAlloca(allocatedType, "");
    }

    public MallocInst createMalloc(Type allocatedType, Value count, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        MallocInst malloc = new MallocInst(insertPoint, id, allocatedType, count, name);

        insertPoint.insertValue(malloc);
        insertPoint.getParent().addValue(malloc);

        return malloc;
    }

    public MallocInst createMalloc(Type allocatedType, Value count) {
        return createMalloc(allocatedType, count, "");
    }

    public FreeInst createFree(Value ptr) {
        int id = insertPoint.getParent().getValueCount();

        FreeInst free = new FreeInst(insertPoint, id, ptr);

        insertPoint.insertValue(free);
        insertPoint.getParent().addValue(free);

        return free;
    }

    public AddrOfInst createAddrOf(AllocaInst alloca, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        AddrOfInst addr = new AddrOfInst(insertPoint, id, alloca, name);

        insertPoint.insertValue(addr);
        insertPoint.getParent().addValue(addr);

        return addr;
    }

    public AddrOfInst createAddrOf(AllocaInst alloca) {
        return createAddrOf(alloca, "");
    }

    public StoreInst createStore(Value ptr, Value value) {
        int id = insertPoint.getParent().getValueCount();
        StoreInst store = new StoreInst(insertPoint, id, ptr, value);

        insertPoint.insertValue(store);
        insertPoint.getParent().addValue(store);

        return store;
    }

    public LoadInst createLoad(Value ptr, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        LoadInst load = new LoadInst(insertPoint, id, ptr, name);

        insertPoint.insertValue(load);
        insertPoint.getParent().addValue(load);

        return load;
    }

    public LoadInst createLoad(Value ptr) {
        return createLoad(ptr, "");
    }

    public ArrayGEPInst createGEP(Type type, Value ptr, Value index, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        ArrayGEPInst gep = new ArrayGEPInst(insertPoint, id, type, ptr, index, name);

        insertPoint.insertValue(gep);
        insertPoint.getParent().addValue(gep);

        return gep;
    }

    public ArrayGEPInst createGEP(Type type, Value ptr, Value index) {
        return createGEP(type, ptr, index, "");
    }

    public StructGEPInst createStructGEP(Type type, Value structPtr, int memberIndex, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        StructGEPInst gep = new StructGEPInst(insertPoint, id, type, structPtr, memberIndex, name);

        insertPoint.insertValue(gep);
        insertPoint.getParent().addValue(gep);

        return gep;
    }

    public StructGEPInst createStructGEP(Type type, Value structPtr, int memberIndex) {
        return createStructGEP(type, structPtr, memberIndex, "");
    }

    public BinOpInst createAdd(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.ADD, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createAdd(Value left, Value right) {
        return createAdd(left, right, "");
    }

    public BinOpInst createSub(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.SUB, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createSub(Value left, Value right) {
        return createSub(left, right, "");
    }

    public BinOpInst createMul(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.MUL, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createMul(Value left, Value right) {
        return createMul(left, right, "");
    }

    public BinOpInst createDiv(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.DIV, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createDiv(Value left, Value right) {
        return createDiv(left, right, "");
    }

    public BinOpInst createCmpEq(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.EQ, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createCmpEq(Value left, Value right) {
        return createCmpEq(left, right, "");
    }

    public BinOpInst createCmpNe(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.NE, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createCmpNe(Value left, Value right) {
        return createCmpNe(left, right, "");
    }

    public BinOpInst createCmpLt(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.LT, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createCmpLt(Value left, Value right) {
        return createCmpLt(left, right, "");
    }

    public BinOpInst createCmpGt(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.GT, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createCmpGt(Value left, Value right) {
        return createCmpGt(left, right, "");
    }

    public BinOpInst createCmpLte(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.LTE, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createCmpLte(Value left, Value right) {
        return createCmpLte(left, right, "");
    }

    public BinOpInst createCmpGte(Value left, Value right, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        BinOpInst binOp = new BinOpInst(insertPoint, id, left, BinOpInst.Operator.GTE, right, name);

        insertPoint.insertValue(binOp);
        insertPoint.getParent().addValue(binOp);

        return binOp;
    }

    public BinOpInst createCmpGte(Value left, Value right) {
        return createCmpGte(left, right, "");
    }

    public BranchInst createBr(Block destination) {
        int id = insertPoint.getParent().getValueCount();

        BranchInst branch = new BranchInst(insertPoint, id, destination);

        insertPoint.insertValue(branch);
        insertPoint.getParent().addValue(branch);

        return branch;
    }

    public BranchInst createCondBr(Value condition, Block trueBranch, Block falseBranch) {
        int id = insertPoint.getParent().getValueCount();

        BranchInst branch = new BranchInst(insertPoint, id, condition, trueBranch, falseBranch);

        insertPoint.insertValue(branch);
        insertPoint.getParent().addValue(branch);

        return branch;
    }

    public UnOpInst createPos(Value operand, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        UnOpInst unOp = new UnOpInst(insertPoint, id, operand, UnOpInst.Operator.POS, name);

        insertPoint.insertValue(unOp);
        insertPoint.getParent().addValue(unOp);

        return unOp;
    }

    public UnOpInst createPos(Value operand) {
        return createPos(operand, "");
    }

    public UnOpInst createNeg(Value operand, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        UnOpInst unOp = new UnOpInst(insertPoint, id, operand, UnOpInst.Operator.NEG, name);

        insertPoint.insertValue(unOp);
        insertPoint.getParent().addValue(unOp);

        return unOp;
    }

    public UnOpInst createNeg(Value operand) {
        return createNeg(operand, "");
    }

    public ConstantInt createConstantInt(long value, Type type) {
        int id = insertPoint.getParent().getValueCount();

        ConstantInt constant = new ConstantInt(insertPoint, id, value, type);

        insertPoint.insertValue(constant);
        insertPoint.getParent().addValue(constant);

        return constant;
    }

    public ConstantString createConstantString(String value, Type type) {
        int id = insertPoint.getParent().getValueCount();

        ConstantString constant = new ConstantString(insertPoint, id, value, type);

        insertPoint.insertValue(constant);
        insertPoint.getParent().addValue(constant);

        return constant;
    }

    public InlineAsm createInlineAsm(String asmCode, List<Value> params) {
        int id = insertPoint.getParent().getValueCount();
        InlineAsm asm = new InlineAsm(insertPoint, id, asmCode, params);

        insertPoint.insertValue(asm);
        insertPoint.getParent().addValue(asm);

        return asm;
    }

    public SaveStackOffset saveStackOffset() {
        int id = insertPoint.getParent().getValueCount();
        SaveStackOffset save = new SaveStackOffset(insertPoint, id);

        insertPoint.insertValue(save);
        insertPoint.getParent().addValue(save);

        return save;
    }

    public SetStackOffset setStackOffset() {
        int id = insertPoint.getParent().getValueCount();
        SetStackOffset set = new SetStackOffset(insertPoint, id);

        insertPoint.insertValue(set);
        insertPoint.getParent().addValue(set);

        return set;
    }
}
