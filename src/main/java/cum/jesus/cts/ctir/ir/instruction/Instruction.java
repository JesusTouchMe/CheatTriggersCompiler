package cum.jesus.cts.ctir.ir.instruction;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

public abstract class Instruction extends Value {
    protected Block parent;

    public Instruction(Module module, Block parent, int id) {
        super(module, id);
        this.parent = parent;
    }

    public void eraseFromParent() {
        parent.eraseValue(id);
    }
}
