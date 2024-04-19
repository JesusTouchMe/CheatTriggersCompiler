package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.fakes.FakeFunctionHandleOperand;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.AlcaInstruction;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.PushInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.OptimizationLevel;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.ctir.ir.misc.SaveStackOffset;
import cum.jesus.cts.ctir.ir.misc.SetStackOffset;
import cum.jesus.cts.ctir.type.FunctionType;
import cum.jesus.cts.ctir.type.Type;
import cum.jesus.cts.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Function extends Global {
    private String name;
    private List<Block> blocks;
    private List<Value> values;
    private List<Integer> args;

    public int instructionCount = 0;
    public int totalStackOffset = 0;

    private Function(FunctionType type, Module module, String name) {
        super(module);

        id = module.getFunctions().size();

        this.name = name;
        this.blocks = new ArrayList<>();
        this.values = new ArrayList<>();
        this.args = new ArrayList<>();

        super.type = type;

        int[] argRegisters = { Register.regC, Register.regD, Register.regF, Register.regG};

        int i = 0;
        int stackArgs = -1;
        for (Type argType : type.getArguments()) {
            int id = instructionCount++;
            Argument arg = new Argument(module, id, argType, String.valueOf(id));
            values.add(arg);
            arg.color = (argRegisters.length > i) ? argRegisters[i++] : stackArgs--; // Argument assumes it's on stack if its register is negative
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

    public List<Argument> getArgs() {
        List<Argument> res = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            res.add(getArgument(i));
        }
        return res;
    }

    public Value getValue(int index) {
        return values.get(index);
    }

    public void setValue(int index, Value value) {
        values.set(index, value);
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
    public List<Integer> getOperands() {
        return new ArrayList<>();
    }

    public Operand getBlockEmittedValue(Block block) {
        return block.getEmittedValue();
    }

    @Override
    public void print(PrintStream stream) {
        if (blocks.isEmpty()) {
            stream.printf("\n\ndeclare public %s @%s(", ((FunctionType) type).getReturnType().getName(), name);
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

        stream.printf("\n\ndefine public %s @%s(", ((FunctionType) type).getReturnType().getName(), name);
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
        return String.format("%s@%s", module.getName(), name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (blocks.isEmpty()) {
            values.add(new ConstantPoolFake(new FakeFunctionHandleOperand(name)));
            emittedValue = new ConstPoolEntryOperand(module.constPoolOffset++);
            return;
        }

        values.add(new FunctionInstructionFake(name));
        values.add(new ConstantPoolFake(new FakeFunctionHandleOperand(name)));

        values.add(new PushInstruction(new Register(Register.regStackBase)));
        values.add(new MovInstruction(new Register(Register.regStackBase), new Register(Register.regStackTop)));
        if (totalStackOffset > 0) {
            Operand stackOffset = new Immediate(totalStackOffset);
            values.add(new AlcaInstruction(stackOffset));
        }

        emittedValue = new ConstPoolEntryOperand(module.constPoolOffset++);

        for (int arg : args) {
            this.values.get(arg).emit(values);
        }

        for (Block block : blocks) {
            block.emit(values);
        }
        for (Block block : blocks) {
            block.emitInstructions(values);
        }
    }

    @Override
    public void optimize(OptimizationLevel level) {
        allocateRegisters();
        sortAllocas();

        if (level == OptimizationLevel.NONE) {
            return;
        }

        switch (level) {
            case HIGH:
                optimizeHigh();
            case MEDIUM:
                optimizeMedium();
            case LOW:
                optimizeLow();
                break;

            case SIZE:
                optimizeSize();
                break;
        }
    }

    private void optimizeHigh() {
        for (Block block : blocks) {
            block.optimizeHigh();
        }
    }

    private void optimizeMedium() {
        for (Block block : blocks) {
            block.optimizeMedium();
        }
    }

    private void optimizeLow() {
        for (Block block : blocks) {
            block.optimizeLow();
        }
    }

    private void optimizeSize() {
        optimizeLow(); // TODO possibly change later?

        for (Block block : blocks) {
            block.optimizeSize();
        }
    }

    @Override
    public Operand getEmittedValue() {
        return emittedValue.clone();
    }

    private void allocateRegisters() {
        List<Pair<Integer, Boolean>> allNodes = new ArrayList<>();
        List<Integer> liveNodes = new ArrayList<>();

        for (int arg : args) {
            for (int id : liveNodes) {
                values.get(arg).edges.add(new Pair<>(id, true));
                values.get(id).edges.add(new Pair<>(arg, true));
            }
            liveNodes.add(arg);
            allNodes.add(new Pair<>(arg, true));
        }

        for (Block block : blocks) {
            for (int instruction : block.getInstructions()) {
                if (values.get(instruction).requiresRegister()) {
                    for (int id : liveNodes) {
                        values.get(instruction).edges.add(new Pair<>(id, true));
                        values.get(id).edges.add(new Pair<>(instruction, true));
                    }
                    liveNodes.add(instruction);
                    allNodes.add(new Pair<>(instruction, true));
                }

                for (int operand : values.get(instruction).getOperands()) {
                    Iterator<Integer> it = liveNodes.iterator();
                    while (it.hasNext()) {
                        int n = it.next();
                        if (n == operand) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }

        for (int liveNode : liveNodes) {
            for (Pair<Integer, Boolean> node : allNodes) {
                Optional<Pair<Integer, Boolean>> it = values.get(node.first).edges.stream().filter(edge -> edge.first == liveNode).findFirst();

                if (it.isPresent()) {
                    values.get(node.first).edges.remove(it.get());
                }
            }
        }

        Stack<Integer> stack = new Stack<>();
        final int k = 8;

        int count = allNodes.size();
        int iterations = 0;
        while (count != 0) {
            iterations++;
            for (Pair<Integer, Boolean> it : allNodes) {
                if (it.second && values.get(it.first).edges.size() < k) {
                    stack.push(it.first);
                    for (Pair<Integer, Boolean> node : allNodes) {
                        if (!node.equals(it)) {
                            Optional<Pair<Integer, Boolean>> edge = values.get(node.first).edges.stream().filter(e -> values.get(e.first).getId() == it.first).findFirst();

                            if (edge.isPresent()) {
                                edge.get().second = false;
                            }
                        }
                    }
                    it.second = false;
                    count -= 1;
                }
            }

            if (iterations >= 10000) {
                break; // tmp
            }
        }

        final String[] registers = {
                "regA", "regB", "regC",
                "regD", "regE", "regF",
                "regG", "regH"
        };
        final String[] colors = {
                "red", "blue", "green",
                "yellow", "purple", "orange",
                "cyan", "magenta"
        };

        //try (FileWriter graphout = new FileWriter("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Compiler\\ctir.dot", true)) {
        //try (FileWriter graphout = new FileWriter("C:\\Users\\Jannik\\IdeaProjects\\CheatTriggersCompiler\\ctir.dot", true)) {
        try (FileWriter graphout = new FileWriter("ctir.dot", true)) {
            graphout.write("\n\nstrict graph {");

            while (!stack.empty()) {
                int id = stack.pop();

                for (Pair<Integer, Boolean> edge : values.get(id).edges) {
                    graphout.write("\n\tN" + id + " -- N" + edge.first);
                }

                int color = (values.get(id).color < 0) ? -1 : values.get(id).color;
                if (color == -1) {
                    color = 1; // 0 is special ed prefix buffer, start with 1 :D
                    while (color < k) {
                        int finalColor = color;
                        Optional<Pair<Integer, Boolean>> it = values.get(id).edges.stream().filter(edge -> values.get(edge.first).color == finalColor).findFirst();

                        if (!it.isPresent()) {
                            break;
                        }
                        color++;
                    }
                    if (!(values.get(id) instanceof Argument)) {
                        values.get(id).color = color;
                    }
                }
                values.get(id).register = registers[color - 1];

                graphout.write("\n\tN" + id + " [color=" + colors[color - 1] + "]");
            }
            graphout.write("\n}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortAllocas() {
        List<Value> temp = new ArrayList<>();

        for (Block block : blocks) {
            for (int instruction : block.getInstructions()) {
                if (values.get(instruction) instanceof AllocaInst || values.get(instruction) instanceof SaveStackOffset || values.get(instruction) instanceof SetStackOffset) {
                    temp.add(values.get(instruction));
                }
            }
        }

        //temp.sort((lhs, rhs) -> Integer.compare(rhs.getAllocatedType().getSize(), lhs.getAllocatedType().getSize()));

        int offset = 0;
        int maxOffset = 0;
        Stack<Integer> savedOffset = new Stack<>();
        for (Value alloca : temp) {
            if (alloca instanceof SaveStackOffset) {
                savedOffset.push(offset);
            } else if (alloca instanceof SetStackOffset) {
                offset = savedOffset.pop();
            } else {
                ((AllocaInst) alloca).setStackOffset(allocaSignature, (short) (offset + 1));
                offset += ((AllocaInst) alloca).getAllocatedType().getSize();
                maxOffset = Math.max(maxOffset, offset);
            }
        }

        totalStackOffset = Math.max(offset, maxOffset);
    }

    public static final class AllocaSignature {
        private AllocaSignature() {}
    }
    private static final AllocaSignature allocaSignature = new AllocaSignature();
}
