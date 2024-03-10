package cum.jesus.cts.ctir;

import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.nooperandinstruction.RetInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.IntInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CallInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
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
    private List<Function> constructors;
    private Map<String, Integer> strings;

    public int constPoolOffset = 1;

    public Module(String name) {
        this.name = name;
        this.functions = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.strings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<Function> getConstructors() {
        return constructors;
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

    public void insertConstructor(Function constructor) {
        if (!hasFunction(constructor.getName())) {
            throw new RuntimeException("Cannot make function a constructor before inserting it");
        }
        constructors.add(constructor);
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

        if (!constructors.isEmpty()) { // .constructor function
            stream.println("\n\ndefine private void @.constructor() {");

            int id = 0;
            for (Function func : constructors) {
                stream.printf("    %%%d = call void %s()\n", id++, func.ident());
            }

            stream.println("    ret void");
            stream.print("}");
        }

        // .start function
        stream.println("\n\ndefine private void @.start() {");
        stream.println("    %0 = i32 ctir.vm.getters.argc");
        stream.println("    %1 = i8** ctir.vm.getters.argv");
        stream.println("    %2 = call i32 @main(i32 %0, i8** %1)");
        stream.println("    ctir.vm.signal.exit(i32 %2)");
        stream.print("}");
    }

    public void emit(OutputStream stream) throws IOException {
        List<AsmValue> values = new ArrayList<>();

        for (Function function : functions) {
            function.emit(values);
        }

        // .constructor function
        if (!constructors.isEmpty()) {
            values.add(new FunctionInstructionFake(".constructor"));

            for (Function func : constructors) {
                values.add(new CallInstruction(new ConstPoolEntryOperand(0), func.getEmittedValue()));
            }

            values.add(new RetInstruction());
        }

        // .start function
        int mainFunction = getFunctionByName("main");

        values.add(new FunctionInstructionFake(".start"));

        if (mainFunction != -1) {
            values.add(new CallInstruction(new ConstPoolEntryOperand(0), getFunctionEmittedValue(mainFunction)));
        } else {
            StringOperand failMsg = new StringOperand("No main function was located in this module\n");
            values.add(new MovInstruction(Register.get("regC"), failMsg));
            values.add(new IntInstruction(0x04)); // 0x04 = write, https://docs.google.com/spreadsheets/d/1hRenVVeyh3f27tRenfae8wAnFxkpI1cn3Jle5Rtt5TA/edit?usp=sharing
            values.add(new MovInstruction(Register.get("regE"), new Immediate(-1)));
        }

        values.add(new IntInstruction(0x01)); // 0x01 = exit

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
