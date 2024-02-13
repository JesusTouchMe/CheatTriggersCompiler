package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.ctir.ir.constant.ConstantInt;
import cum.jesus.cts.ctir.ir.instruction.*;
import cum.jesus.cts.type.Type;

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

    public CallInst createCall(Value callee, List<Value> parameters, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        CallInst call = new CallInst(insertPoint, id, name, callee, parameters);

        insertPoint.insertValue(call);
        insertPoint.getParent().addValue(call);

        return call;
    }

    public CallInst createCall(Value callee, List<Value> parameters) {
        return createCall(callee, parameters, "");
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

    public ConstantInt createConstantInt(long value, Type type, String name) {
        int id = insertPoint.getParent().getValueCount();
        if (name.isEmpty()) {
            name = String.valueOf(id);
        }

        ConstantInt constant = new ConstantInt(insertPoint, id, value, type, name);

        insertPoint.insertValue(constant);
        insertPoint.getParent().addValue(constant);

        return constant;
    }

    public ConstantInt createConstantInt(long value, Type type) {
        return createConstantInt(value, type, "");
    }
}
