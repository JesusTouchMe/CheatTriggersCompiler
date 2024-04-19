package cum.jesus.cts.ctir;

import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Label;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.nooperandinstruction.RetInstruction;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.IntInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CallInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.CstInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.ModInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.GlobalVariable;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.LoadInst;
import cum.jesus.cts.ctir.type.Type;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public final class Module {
    private String name;
    private Map<String, Integer> dependencies;
    private List<Function> functions;
    private Map<String, GlobalVariable> globals;
    private List<Function> constructors;
    private Map<String, Integer> strings;

    public int constPoolOffset = 1;

    public Module(String name) {
        this.name = name;
        this.dependencies = new HashMap<>();
        this.functions = new ArrayList<>();
        this.globals = new HashMap<>();
        this.constructors = new ArrayList<>();
        this.strings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public Map<String, GlobalVariable> getGlobals() {
        return globals;
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

    public void addImport(String name) {
        dependencies.put(name, constPoolOffset++);
    }

    public int getImport(String name) {
        return dependencies.getOrDefault(name, 0);
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

    public void insertGlobal(String name, Type type) {
        globals.put(name, new GlobalVariable(this, type, false, name));
    }

    public GlobalVariable getGlobal(String name) {
        return globals.get(name);
    }

    public GlobalVariable getOrInsertGlobal(String name, Type type) {
        GlobalVariable global = globals.get(name);
        if (global == null) {
            global = new GlobalVariable(this, type, false, name);
            globals.put(name, global);
        }

        return global;
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

        if (!globals.isEmpty()) {
            stream.print("\n\n");
        }

        for (Iterator<GlobalVariable> it = globals.values().iterator(); it.hasNext(); ) {
            GlobalVariable global = it.next();
            global.print(stream);
            if (global.prints && it.hasNext()) {
                stream.println();
            }
        }

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

        values.add(new Label(".data"));
        for (GlobalVariable global : globals.values()) {
            global.emit(values);
        }

        for (Function function : functions) {
            function.emit(values);
        }

        if (!dependencies.isEmpty() || !globals.isEmpty() || !constructors.isEmpty()) { // .constructor function
            values.add(new FunctionInstructionFake(".constructor"));

            for (Map.Entry<String, Integer> dependency : dependencies.entrySet()) {
                String str = dependency.getKey();

                if (!hasString(str)) {
                    values.add(new ConstantPoolFake(new StringOperand(str)));
                    insertString(str);
                }

                values.add(new ModInstruction(Register.get("regA"), new ConstPoolEntryOperand(getString(str))));
                values.add(new CstInstruction(new ConstPoolEntryOperand(dependency.getValue()), Register.get("regA")));
            }

            for (Function func : constructors) {
                values.add(new CallInstruction(new ConstPoolEntryOperand(0), func.getEmittedValue()));
            }

            values.add(new RetInstruction());
        }


        // .start function
        int mainFunction = getFunctionByName("main");

        if (mainFunction != -1) {
            values.add(new FunctionInstructionFake(".start"));
            values.add(new CallInstruction(new ConstPoolEntryOperand(0), getFunctionEmittedValue(mainFunction)));
            values.add(new MovInstruction(Register.get("regC"), Register.get("regE")));
            values.add(new IntInstruction(0x01)); // 0x01 = exit
        }

        OutputBuffer output = new OutputBuffer(name);
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
