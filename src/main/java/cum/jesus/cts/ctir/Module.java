package cum.jesus.cts.ctir;

import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.LoadInst;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Module {
    private String name;
    private List<Function> functions;
    private Map<String, Integer> strings;

    public int constPoolOffset = 1;

    public Module(String name) {
        this.name = name;
        this.functions = new ArrayList<>();
        this.strings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public boolean hasFunction(String name) {
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Function getFunction(int id) {
        return functions.get(id);
    }

    public int getFunctionByName(String name) {
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                return func.getId();
            }
        }
        return -1;
    }

    public void insertFunction(Function func) {
        functions.add(func);
    }

    public Operand getFunctionEmittedValue(int id) {
        return functions.get(id).getEmittedValue();
    }

    public Map<String, Integer> getStrings() {
        return strings;
    }

    public boolean hasString(String string) {
        return strings.containsKey(string);
    }

    public int getString(String string) {
        return strings.getOrDefault(string, -1);
    }

    public int insertString(String string) {
        strings.put(string, constPoolOffset++);
        return strings.get(string);
    }

    public void print(PrintStream stream) {
        stream.printf("module \"%s\"", name);
        for (Function func : functions) {
            func.print(stream);
        }
        StartFunction.print(stream);
    }

    public void emit(OutputStream stream) throws IOException {
        List<AsmValue> values = new ArrayList<>();

        for (Function function : functions) {
            function.emit(values);
        }

        StartFunction.emit(values, this);

        OutputBuffer output = new OutputBuffer();
        OpcodeBuilder builder = new OpcodeBuilder(output);

        for (AsmValue value : values) {
            value.print(System.out);
            value.emit(builder);
        }

        builder.patchForwardLabels();
        output.emit(stream);
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
