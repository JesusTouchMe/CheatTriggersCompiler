package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.OptimizationLevel;
import cum.jesus.cts.type.FunctionType;
import cum.jesus.cts.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Function extends Value {
    private String name;
    private List<Block> blocks;
    private List<Value> values;
    private List<Integer> args;

    public int instructionCount = 0;
    private int totalStackOffset = 0;

    private Function(FunctionType type, Module module, String name) {
        super(module, module.getFunctions().size());

        this.name = name;
        this.blocks = new ArrayList<>();
        this.values = new ArrayList<>();
        this.args = new ArrayList<>();

        super.type = type;

        int[] argRegisters = { 3, 4, 6, 7};

        int i = 0;
        for (Type argType : type.getArgs()) {
            int id = instructionCount++;
            Argument arg = new Argument(module, id, argType, String.valueOf(id));
            values.add(arg);
            arg.register = argRegisters.length > i ? argRegisters[i++] : -1; // Argument assumes it's on stack if its register is -1
            args.add(id);
        }
    }

    public static Function create(FunctionType type, Module module, String name) {
        Function func = new Function(type, module, name);
        module.insertFunction(func);
        return func;
    }

    public Module getModule() {
        return module;
    }

    public Type getReturnType() {
        return ((FunctionType) type).getReturnType();
    }

    public String getName() {
        return name;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public Value getValue(int index) {
        return values.get(index);
    }

    public void insertBlock(Block block) {
        blocks.add(block);
    }

    public int getValueCount() {
        return values.size();
    }

    public Argument getArgument(int index) {
        return (Argument) (values.get(args.get(index)));
    }

    public void addValue(Value value) {
        values.add(value);
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public void print(PrintStream stream) {
        if (blocks.isEmpty()) {
            stream.printf("\n\ndeclare public %s %%%d@%s(", ((FunctionType) type).getReturnType().getName(), id, name);
            Iterator<Integer> iterator = args.iterator();
            while (iterator.hasNext()) {
                stream.print(values.get(iterator.next()).ident());
                if (iterator.hasNext()) {
                    stream.print(", ");
                }
            }
            stream.print(");");
            return;
        }

        stream.printf("\n\ndefine public %s %%%d@%s(", ((FunctionType) type).getReturnType().getName(), id, name);
        Iterator<Integer> iterator = args.iterator();
        while (iterator.hasNext()) {
            stream.print(values.get(iterator.next()).ident());
            if (iterator.hasNext()) {
                stream.print(", ");
            }
        }
        stream.print(") {\n");

        for (Block block : blocks) {
            block.print(stream);
        }

        stream.print("}");
    }

    @Override
    public String ident() {
        return String.format("%%%d@%s", id, name);
    }

    @Override
    public void emit(List<AsmValue> values) {

    }

    public void optimize(OptimizationLevel level) {
        if (level == OptimizationLevel.NONE) {
            return;
        }
    }
}
