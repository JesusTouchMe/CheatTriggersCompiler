package cum.jesus.cts.ctir.ir.constant;

import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;

public abstract class Constant extends Value {
    protected Block parent;

    public Constant(Block parent, int id) {
        super(parent.getParent().getModule(), id);

        this.parent = parent;
    }

    public void eraseFromParent() {
        parent.eraseValue(id);
    }
}
