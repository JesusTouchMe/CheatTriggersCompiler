package cum.jesus.cts.asm.codegen.builder;

import cum.jesus.cts.asm.codegen.OperandSize;
import cum.jesus.cts.asm.codegen.OutputBuffer;

import java.util.ArrayList;
import java.util.List;

public final class OpcodeBuilder {
    private OutputBuffer output;
    private List<ForwardLabel> forwardLabels;

    public OpcodeBuilder(OutputBuffer output) {
        this.output = output;
        this.forwardLabels = new ArrayList<>();
    }

    public void patchForwardLabels() {
        for (ForwardLabel label : forwardLabels) {
            output.patchForwardSymbol(label.name, label.size, label.offset, label.position);
        }
    }

    public Instruction createInstruction() {
        return new Instruction(output);
    }

    public void addLabel(String name) {
        output.addSymbol(name, output.getPosition());
    }

    public void forwardLabel(String name, OperandSize size, int offset) {
        forwardLabels.add(new ForwardLabel(name, size, output.getPosition() + offset, output.getPosition()));
    }

    public int getLabel(String name) {
        return output.getSymbol(name);
    }

    public int getPosition() {
        return output.getPosition();
    }
}
