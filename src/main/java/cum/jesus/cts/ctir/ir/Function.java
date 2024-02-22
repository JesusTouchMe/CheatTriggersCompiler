package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.fakes.FakeFunctionHandleOperand;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.operand.ConstPoolEntryOperand;
import cum.jesus.cts.asm.instruction.operand.Immediate;
import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.asm.instruction.operand.StringOperand;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.AlcaInstruction;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.PushInstruction;
import cum.jesus.cts.asm.instruction.twooperandinstruction.MovInstruction;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.OptimizationLevel;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.type.FunctionType;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Function extends Value {
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

        int[] argRegisters = { Register.regC, Register.regD, Register.regF, Register.regG};

        int i = 0;
        for (Type argType : type.getArgs()) {
            int id = instructionCount++;
            Argument arg = new Argument(module, id, argType, String.valueOf(id));
            values.add(arg);
            arg.color = argRegisters.length > i ? argRegisters[i++] : -1; // Argument assumes it's on stack if its register is -1
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
        return String.format("@%s", name);
    }

    @Override
    public void emit(List<AsmValue> values) {
        if (blocks.isEmpty()) {
            return;
        }

        allocateRegisters();
        sortAllocas();

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

    public void optimize(OptimizationLevel level) {
        if (level == OptimizationLevel.NONE) {
            return;
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
        while (count != 0) {
            for (int i = 0; i < allNodes.size(); i++) {
                Pair<Integer, Boolean> it = allNodes.get(i);

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

        try (FileWriter graphout = new FileWriter("C:\\Users\\Jannik\\IdeaProjects\\CheatTriggersCompiler\\ctir.dot", true)) {
            graphout.write("\n\nstrict graph {");

            while (!stack.empty()) {
                int id = stack.pop();

                for (Pair<Integer, Boolean> edge : values.get(id).edges) {
                    graphout.write("\n\tN" + id + " -- N" + edge.first);
                }

                int color = values.get(id).color;
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
                    values.get(id).color = color;
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
        List<AllocaInst> temp = new ArrayList<>();

        for (Block block : blocks) {
            for (int instruction : block.getInstructions()) {
                if (values.get(instruction) instanceof AllocaInst) {
                    temp.add((AllocaInst) values.get(instruction));
                }
            }
        }

        //temp.sort((lhs, rhs) -> Integer.compare(rhs.getAllocatedType().getSize(), lhs.getAllocatedType().getSize()));

        int offset = 0;
        for (AllocaInst alloca : temp) {
            offset += 1; // an alloca is 1 value aka 1 size cuz vm works in values not bytes
            alloca.setStackOffset(allocaSignature, (short) offset);
        }

        totalStackOffset = offset;
    }

    public static final class AllocaSignature {
        private AllocaSignature() {}
    }
    private static final AllocaSignature allocaSignature = new AllocaSignature();
}
