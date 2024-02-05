package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;
import java.util.List;

public abstract class Value {
    protected Module module;
    protected int id;
    protected Type type;

    protected Operand emittedValue;

    protected int register = -1;

    public Value(Module module, int id) {
        this.module = module;
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    public abstract boolean requiresRegister();

    public abstract void print(PrintStream stream);

    public abstract String ident();

    public abstract void emit(List<AsmValue> values);

    public Operand getEmittedValue() {
        assert emittedValue != null;
        return emittedValue;
    }
}
