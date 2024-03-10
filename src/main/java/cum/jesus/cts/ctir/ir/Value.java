package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.type.Type;
import cum.jesus.cts.util.Pair;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Value {
    protected Module module;
    protected int id;
    protected Type type;
    protected Operand emittedValue;

    protected List<Pair<Integer, Boolean>> edges;
    public int color = -1;
    protected String register = "";

    public Value(Module module, int id) {
        this.module = module;
        this.id = id;
        this.edges = new ArrayList<>();
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

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public abstract boolean requiresRegister();

    public abstract List<Integer> getOperands();

    public abstract void print(PrintStream stream);

    public abstract String ident();

    public abstract void emit(List<AsmValue> values);

    public Operand getEmittedValue() {
        assert emittedValue != null;
        return emittedValue;
    }
}
