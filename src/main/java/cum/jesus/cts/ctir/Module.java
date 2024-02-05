package cum.jesus.cts.ctir;

import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.LoadInst;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class Module {
    private String name;
    private List<Function> functions;

    public Module(String name) {
        this.name = name;
        this.functions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void insertFunction(Function func) {
        functions.add(func);
    }

    public void print(PrintStream stream) {
        stream.printf("filename = \"%s\"", name);
        for (Function func : functions) {
            func.print(stream);
        }
    }

    public void optimize(OptimizationLevel level) {
        if (level == OptimizationLevel.NONE) {
            return;
        }

        for (Function func : functions) {
            func.optimize(level);
        }
    }

    public static Value getPointerOperand(Value value) {
        if (value instanceof LoadInst) {
            return ((LoadInst) value).getPointer();
        }

        return null;
    }
}
